package com.autowash.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class CustomerDTO {
    private Long id;
    private Long userId;
    private String fullName;
    private String phoneNumber;
    private String email;
    private Integer loyaltyPoints;
    private Integer redeemablePoints;
    private String currentTier;
}