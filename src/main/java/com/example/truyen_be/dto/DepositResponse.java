package com.example.truyen_be.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DepositResponse {
    private Boolean success;
    private String message;
    private String transactionId;
    private Integer coins;
    private Integer newBalance;
}