package com.autowash.repository;

import com.autowash.entity.ServicePricing;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ServicePricingRepository extends JpaRepository<ServicePricing, Long> {
    List<ServicePricing> findByVehicleType(String vehicleType);
}
