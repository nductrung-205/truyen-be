package com.example.truyen_be.controller;

import com.example.truyen_be.entity.PaymentTransaction;
import com.example.truyen_be.service.VNPayService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;

@RestController
@RequestMapping("/api/vnpay")
@RequiredArgsConstructor
public class VNPayController {

    private final VNPayService vnpayService;

    @GetMapping("/create-payment")
    public ResponseEntity<?> createPayment(
            @RequestParam Long amount,
            @RequestParam Long userId,
            @RequestParam(defaultValue = "WEB") String platform) {
        try {
            // Truyền 3 tham số: amount, userId, platform
            String url = vnpayService.createPaymentUrl(amount, userId, platform);
            return ResponseEntity.ok(Collections.singletonMap("url", url));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Lỗi: " + e.getMessage());
        }
    }

    @GetMapping("/callback")
    public void callback(@RequestParam Map<String, String> allParams, HttpServletResponse response) throws IOException {
        String vnp_TxnRef = allParams.get("vnp_TxnRef");
        boolean isSuccess = vnpayService.processCallback(allParams);
        String platform = vnpayService.getPlatformByTxnRef(vnp_TxnRef);
        String result = isSuccess ? "success" : "failed";

        if ("APP".equals(platform)) {
            // Tạo URL Deep Link cho Expo
            String deepLink = "myapp://payment-status?result=" + result;

            response.setContentType("text/html;charset=UTF-8");
            String html = "<html>" +
                    "<head><meta name='viewport' content='width=device-width, initial-scale=1.0'></head>" +
                    "<body style='text-align:center; padding-top: 50px; font-family: sans-serif;'>" +
                    "   <h2>Thanh toán " + (isSuccess ? "thành công" : "thất bại") + "</h2>" +
                    "   <p>Vui lòng nhấn nút bên dưới để quay lại ứng dụng.</p>" +
                    "   <a href='" + deepLink
                    + "' style='display:inline-block; background:#007bff; color:white; padding:10px 20px; text-decoration:none; border-radius:5px;'>Quay lại ứng dụng</a>"
                    +
                    "   <script>" +
                    "       // Tự động chuyển hướng sau 1 giây" +
                    "       setTimeout(function() { window.location.href = '" + deepLink + "'; }, 1000);" +
                    "   </script>" +
                    "</body>" +
                    "</html>";
            response.getWriter().write(html);
        } else {
            // Cho Web thì vẫn giữ nguyên redirect cũ
            response.sendRedirect("http://10.18.12.125:8081/profile/deposit?result=" + result);
        }
    }
}