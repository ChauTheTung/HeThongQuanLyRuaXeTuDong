package com.autowash.repository;

import com.autowash.entity.PointTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PointTransactionRepository extends JpaRepository<PointTransaction, Long> {
    List<PointTransaction> findByCustomerId(Long customerId);
    List<PointTransaction> findByCustomerIdAndTransactionTypeAndCreatedAtBefore(Long customerId, String transactionType, LocalDateTime before);
    List<PointTransaction> findByCustomerIdAndCreatedAtAfter(Long customerId, LocalDateTime after);
    PointTransaction findFirstByCustomerIdAndTransactionTypeOrderByCreatedAtDesc(Long customerId, String transactionType);
}
