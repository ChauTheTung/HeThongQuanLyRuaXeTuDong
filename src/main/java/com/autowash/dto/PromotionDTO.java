package com.autowash.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class PromotionDTO {
    private Long id;
    private String name;
    private String description;
    private String tierName;
    private Double discountPercent;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Boolean active;
}
