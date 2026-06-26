package com.autowash.repository;

import com.autowash.entity.Promotion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PromotionRepository extends JpaRepository<Promotion, Long> {
    List<Promotion> findByActiveTrue();
    List<Promotion> findByTierName(String tierName);
    Optional<Promotion> findByNameAndActiveTrue(String name);
}
