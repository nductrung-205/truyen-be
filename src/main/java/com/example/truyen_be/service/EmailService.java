package com.example.truyen_be.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    /**
     * Gửi mã OTP qua email
     */
    public void sendOtpEmail(String toEmail, String otp, String username) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setSubject("🔐 Mã xác thực đặt lại mật khẩu - Truyện App");
            
            String emailBody = String.format(
                "Xin chào %s,\n\n" +
                "Bạn đã yêu cầu đặt lại mật khẩu cho tài khoản của mình.\n\n" +
                "━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n" +
                "   MÃ XÁC THỰC CỦA BẠN: %s\n" +
                "━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n\n" +
                "⏰ Mã này sẽ hết hạn sau 10 phút.\n" +
                "🔒 Vui lòng KHÔNG chia sẻ mã này với bất kỳ ai.\n\n" +
                "Nếu bạn không yêu cầu đặt lại mật khẩu, vui lòng bỏ qua email này " +
                "và đảm bảo tài khoản của bạn vẫn an toàn.\n\n" +
                "Trân trọng,\n" +
                "Truyện App Team 📚",
                username, otp
            );
            
            message.setText(emailBody);
            
            mailSender.send(message);
            System.out.println("✅ OTP đã được gửi đến: " + toEmail);
            
        } catch (Exception e) {
            System.err.println("❌ Lỗi khi gửi email: " + e.getMessage());
            throw new RuntimeException("Không thể gửi email. Vui lòng thử lại sau.");
        }
    }

    /**
     * Gửi thông báo mật khẩu đã được thay đổi
     */
    public void sendPasswordChangedNotification(String toEmail, String username) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setSubject("✅ Mật khẩu đã được thay đổi - Truyện App");
            
            String emailBody = String.format(
                "Xin chào %s,\n\n" +
                "Mật khẩu của bạn đã được thay đổi thành công vào lúc %s.\n\n" +
                "🔒 Nếu bạn không thực hiện thay đổi này, vui lòng liên hệ với " +
                "chúng tôi ngay lập tức để bảo vệ tài khoản của bạn.\n\n" +
                "Trân trọng,\n" +
                "Truyện App Team 📚",
                username, java.time.LocalDateTime.now().format(
                    java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")
                )
            );
            
            message.setText(emailBody);
            mailSender.send(message);
            System.out.println("✅ Email thông báo đã được gửi");
            
        } catch (Exception e) {
            System.err.println("⚠️ Lỗi khi gửi email thông báo: " + e.getMessage());
            // Không throw exception vì đây chỉ là thông báo
        }
    }
}