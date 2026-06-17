package com.autowash.repository;

import com.autowash.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByCustomerId(Long customerId);
    List<Booking> findByStatus(String status);
    List<Booking> findByCustomerIdAndStatus(Long customerId, String status);
    List<Booking> findByCustomerIdIn(List<Long> customerIds);
    List<Booking> findByCustomerIdInAndStatus(List<Long> customerIds, String status);
}

