package com.autowash.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class BookingDTO {
    private Long id;
    private Long customerId;
    private Long vehicleId;
    private LocalDateTime bookingTime;
    private String status;
    private Double totalPrice;
}