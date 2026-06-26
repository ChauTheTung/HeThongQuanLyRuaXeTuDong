package com.autowash.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class NotificationDTO {
    private String title;
    private String content;
    private LocalDateTime time;
    private String type; // success, info, warning, danger
    private String icon; // bi-check-circle, bi-info-circle, bi-star-fill, etc.
}
