package com.example.truyen_be.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // 1. Phải có cấu hình CORS ở đây
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            
            // 2. Tắt CSRF (BẮT BUỘC để gọi POST từ App/Axios)
            .csrf(csrf -> csrf.disable())
            
            // 3. Cấu hình quyền truy cập
            .authorizeHttpRequests(auth -> auth
                // Cho phép tất cả các request OPTIONS (Preflight)
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                
                // Các đường dẫn công khai
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/api/stories/**").permitAll()
                .requestMatchers("/api/users/**").permitAll() // Bao gồm cả check-in
                .requestMatchers("/api/categories/**").permitAll()
                .requestMatchers("/api/chapters/**").permitAll()
                
                // Mọi request khác cần login (nếu bạn chưa làm login thì tạm thời để permitAll hết)
                .anyRequest().permitAll() 
            );

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        // Cấu hình origin cụ thể
        configuration.setAllowedOriginPatterns(Arrays.asList("*")); 
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}