package com.autowash.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "customers")
@Data
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Column(name = "phone_number", unique = true)
    private String phoneNumber;

    private String email;

    private String address;

    private String city;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Column(name = "avatar_url")
    private String avatarUrl;

    @Column(name = "loyalty_points")
    // Accumulated points used to determine membership tier (keeps increasing even after redemption)
    private Integer loyaltyPoints = 0;

    @Column(name = "redeemable_points")
    // Points that can be spent/redeemed by the customer
    private Integer redeemablePoints = 0;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();
}

