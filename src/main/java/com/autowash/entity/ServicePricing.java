package com.autowash.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "service_pricing")
@Data
public class ServicePricing {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "service_name", nullable = false)
    private String serviceName; // VD: Rửa xe tiêu chuẩn, Thay nhớt...

    @Column(name = "vehicle_type", nullable = false)
    private String vehicleType; // VD: Ô tô, Xe máy

    @Column(nullable = false)
    private Double price;

    private String description;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();
}
