package com.autowash.dto;

import lombok.Data;

@Data
public class LoyaltyDTO {
    private Long customerId;
    private Integer loyaltyPoints;
    private String currentTier;
    private Integer nextTierPoints;
}
