package com.autowash.dto;

import lombok.Data;

@Data
public class VehicleDTO {
    private Long id;
    private Long customerId;
    private String licensePlate;
    private String vehicleType;
    private String brand;
    private String color;
    private String notes;
}