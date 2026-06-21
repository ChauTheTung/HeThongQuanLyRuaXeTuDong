package com.autowash.service;

import com.autowash.entity.ServicePricing;
import com.autowash.repository.ServicePricingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PricingService {

    @Autowired
    private ServicePricingRepository pricingRepository;

    public List<ServicePricing> getAllServices() {
        return pricingRepository.findAll();
    }

    public List<ServicePricing> getServicesByVehicleType(String vehicleType) {
        return pricingRepository.findByVehicleType(vehicleType);
    }

    public ServicePricing getServiceById(Long id) {
        return pricingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Service not found: " + id));
    }

    public ServicePricing saveService(ServicePricing service) {
        if (service.getId() == null) {
            service.setCreatedAt(LocalDateTime.now());
        }
        service.setUpdatedAt(LocalDateTime.now());
        return pricingRepository.save(service);
    }

    public void deleteService(Long id) {
        pricingRepository.deleteById(id);
    }
}
