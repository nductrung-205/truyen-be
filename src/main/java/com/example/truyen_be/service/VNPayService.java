package com.example.truyen_be.service;

import com.example.truyen_be.config.VNPayConfig;
import com.example.truyen_be.entity.PaymentTransaction;
import com.example.truyen_be.repository.TransactionRepository;
import com.example.truyen_be.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
@RequiredArgsConstructor
public class VNPayService {

    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;

    public String createPaymentUrl(Long amount, Long userId, String platform) throws Exception {
        try {
            String vnp_Version = "2.1.0";
            String vnp_Command = "pay";
            String vnp_OrderInfo = "Nap tien cho user " + userId;
            String vnp_TxnRef = VNPayConfig.getRandomNumber(8); // Tạo mã đơn hàng ngẫu nhiên
            String vnp_TmnCode = VNPayConfig.vnp_TmnCode;
            

            Map<String, String> vnp_Params = new HashMap<>();
            vnp_Params.put("vnp_Version", vnp_Version);
            vnp_Params.put("vnp_Command", vnp_Command);
            vnp_Params.put("vnp_TmnCode", vnp_TmnCode);
            vnp_Params.put("vnp_Amount", String.valueOf(amount * 100));
            vnp_Params.put("vnp_CurrCode", "VND");
            vnp_Params.put("vnp_TxnRef", vnp_TxnRef);
            vnp_Params.put("vnp_OrderInfo", vnp_OrderInfo);
            vnp_Params.put("vnp_OrderType", "other");
            vnp_Params.put("vnp_Locale", "vn");
            vnp_Params.put("vnp_ReturnUrl", VNPayConfig.vnp_ReturnUrl);
            vnp_Params.put("vnp_IpAddr", "127.0.0.1");

            Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
            SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
            vnp_Params.put("vnp_CreateDate", formatter.format(cld.getTime()));

            List<String> fieldNames = new ArrayList<>(vnp_Params.keySet());
            Collections.sort(fieldNames);
            StringBuilder hashData = new StringBuilder();
            StringBuilder query = new StringBuilder();
            
            for (String fieldName : fieldNames) {
                String fieldValue = vnp_Params.get(fieldName);
                if (fieldValue != null && fieldValue.length() > 0) {
                    // Build hash data
                    hashData.append(fieldName).append('=')
                            .append(URLEncoder.encode(fieldValue, StandardCharsets.UTF_8.toString()));
                    // Build query
                    query.append(URLEncoder.encode(fieldName, StandardCharsets.UTF_8.toString()))
                            .append('=')
                            .append(URLEncoder.encode(fieldValue, StandardCharsets.UTF_8.toString()));
                    
                    if (fieldNames.indexOf(fieldName) < fieldNames.size() - 1) {
                        query.append('&');
                        hashData.append('&');
                    }
                }
            }

            String queryUrl = query.toString();
            String vnp_SecureHash = VNPayConfig.hmacSHA512(VNPayConfig.vnp_HashSecret, hashData.toString());
            queryUrl += "&vnp_SecureHash=" + vnp_SecureHash;

            // LƯU VÀO DATABASE
            PaymentTransaction transaction = PaymentTransaction.builder()
                    .txnRef(vnp_TxnRef)
                    .userId(userId)
                    .amount(amount)
                    .status("PENDING")
                    .platform(platform)
                    .build();
            transactionRepository.save(transaction);

            return VNPayConfig.vnp_PayUrl + "?" + queryUrl;
        } catch (Exception e) {
            e.printStackTrace(); // IN LỖI RA CONSOLE ĐỂ DEBUG
            throw e;
        }
    }

    public String getPlatformByTxnRef(String txnRef) {
        return transactionRepository.findByTxnRef(txnRef)
                .map(PaymentTransaction::getPlatform)
                .orElse("WEB"); // Nếu không tìm thấy mặc định là WEB
    }

    @Transactional
    public boolean processCallback(Map<String, String> fields) {
        String vnp_ResponseCode = fields.get("vnp_ResponseCode");
        String vnp_TxnRef = fields.get("vnp_TxnRef");

        if ("00".equals(vnp_ResponseCode)) {
            Optional<PaymentTransaction> transOpt = transactionRepository.findByTxnRef(vnp_TxnRef);
            if (transOpt.isPresent()) {
                PaymentTransaction transaction = transOpt.get();
                if ("PENDING".equals(transaction.getStatus())) {
                    transaction.setStatus("SUCCESS");
                    transactionRepository.save(transaction);

                    userRepository.findById(transaction.getUserId()).ifPresent(user -> {
                        int coinsToAdd = (int) (transaction.getAmount() / 1000);
                        user.setCoins((user.getCoins() == null ? 0 : user.getCoins()) + coinsToAdd);
                        userRepository.save(user);
                    });
                    return true;
                }
            }
        }
        return false;
    }
}