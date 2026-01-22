package com.example.truyen_be.dto;

import lombok.Data;

@Data
public class DepositRequest {
    private Integer amount;
    private Integer coins;
    private Integer bonus;
}