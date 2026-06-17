package com.autowash.repository;

import com.autowash.entity.LoyaltyTier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LoyaltyTierRepository extends JpaRepository<LoyaltyTier, Long> {
    Optional<LoyaltyTier> findByName(String name);
    Optional<LoyaltyTier> findByMinPointsLessThanEqualAndMaxPointsGreaterThanEqual(Integer minPoints, Integer maxPoints);
    Optional<LoyaltyTier> findFirstByMinPointsGreaterThanOrderByMinPointsAsc(Integer points);
}
