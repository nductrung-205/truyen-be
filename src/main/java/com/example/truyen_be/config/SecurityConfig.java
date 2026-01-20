package com.example.truyen_be.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        // Cho phép truy cập công khai
                        .requestMatchers("/api/auth/**").permitAll() // Đăng ký/đăng nhập
                        .requestMatchers("/api/stories/**").permitAll() // Xem truyện
                        .requestMatchers("/api/categories/**").permitAll() // ✅ THÊM DÒNG NÀY
                        .requestMatchers("/api/chapters/**").permitAll() // Đọc chương
                        .requestMatchers("/api/search/**").permitAll() // Tìm kiếm

                        // Các endpoint khác cần đăng nhập
                        .anyRequest().authenticated());

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // ❌ Thay vì setAllowedOrigins, hãy dùng setAllowedOriginPatterns
        configuration.setAllowedOriginPatterns(Arrays.asList(
                "http://localhost:8081",
                "http://localhost:19006",
                "http://127.0.0.1:8081",
                "http://192.168.*:*", // ✅ Đúng cú pháp cho patterns
                "*" // ✅ Hoặc cho phép tất cả để test nhanh
        ));

        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}