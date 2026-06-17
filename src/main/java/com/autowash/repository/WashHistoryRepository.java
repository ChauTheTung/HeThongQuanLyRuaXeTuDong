package com.autowash.repository;

import com.autowash.entity.WashHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WashHistoryRepository extends JpaRepository<WashHistory, Long> {
    List<WashHistory> findByCustomerId(Long customerId);
    List<WashHistory> findByVehicleId(Long vehicleId);
}
