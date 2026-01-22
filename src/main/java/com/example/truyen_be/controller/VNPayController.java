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

        // Hết lỗi vì hàm này đã được định nghĩa trong VNPayService ở bước trên
        String platform = vnpayService.getPlatformByTxnRef(vnp_TxnRef);

        String result = isSuccess ? "success" : "failed";

        if ("APP".equals(platform)) {
            // Trả về Deep Link cho Expo App
            response.sendRedirect("myapp://payment-status?result=" + result);
        } else {
            // Trả về URL cho trình duyệt Web
            response.sendRedirect("http://192.168.1.14:8081/profile/deposit?result=" + result);
        }
    }
}