package com.example.truyen_be.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "transactions")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentTransaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // SỬA DÒNG NÀY: Khớp chính xác với tên cột trong DB của bạn
    @Column(name = "vnp_txn_ref", nullable = false) 
    private String txnRef; 

    @Column(name = "user_id")
    private Long userId;

    private Long amount;
    
    private String status; // PENDING, SUCCESS, FAILED
    
    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    private String platform;
}