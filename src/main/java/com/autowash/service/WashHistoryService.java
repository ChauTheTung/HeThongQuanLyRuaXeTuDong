package com.autowash.service;

import com.autowash.entity.WashHistory;
import com.autowash.repository.WashHistoryRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class WashHistoryService {

    private final WashHistoryRepository washHistoryRepository;

    public WashHistoryService(WashHistoryRepository washHistoryRepository) {
        this.washHistoryRepository = washHistoryRepository;
    }

    public WashHistory recordWashHistory(WashHistory history) {
        if (history.getPerformedAt() == null) {
            history.setPerformedAt(LocalDateTime.now());
        }
        return washHistoryRepository.save(history);
    }

    public List<WashHistory> getWashHistoryByCustomer(Long customerId) {
        return washHistoryRepository.findByCustomerId(customerId);
    }

    public List<WashHistory> getWashHistoryByVehicle(Long vehicleId) {
        return washHistoryRepository.findByVehicleId(vehicleId);
    }
}
