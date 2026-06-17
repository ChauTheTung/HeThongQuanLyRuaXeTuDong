package com.autowash.service;

import com.autowash.entity.Promotion;
import com.autowash.exception.ResourceNotFoundException;
import com.autowash.repository.PromotionRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PromotionService {

    private final PromotionRepository promotionRepository;

    public PromotionService(PromotionRepository promotionRepository) {
        this.promotionRepository = promotionRepository;
    }

    public Promotion createPromotion(Promotion promotion) {
        promotion.setActive(Boolean.TRUE.equals(promotion.getActive()));
        promotion.setCreatedAt(LocalDateTime.now());
        promotion.setUpdatedAt(LocalDateTime.now());
        return promotionRepository.save(promotion);
    }

    public List<Promotion> getAllPromotions() {
        return promotionRepository.findAll();
    }

    public List<Promotion> getActivePromotions() {
        return promotionRepository.findByActiveTrue();
    }

    public List<Promotion> getPromotionsForTier(String tierName) {
        return promotionRepository.findByTierName(tierName);
    }

    public Promotion updatePromotion(Long id, Promotion update) {
        Promotion promotion = promotionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Promotion not found with id: " + id));

        promotion.setName(update.getName());
        promotion.setDescription(update.getDescription());
        promotion.setDiscountPercent(update.getDiscountPercent());
        promotion.setTierName(update.getTierName());
        promotion.setStartDate(update.getStartDate());
        promotion.setEndDate(update.getEndDate());
        promotion.setActive(Boolean.TRUE.equals(update.getActive()));
        promotion.setUpdatedAt(LocalDateTime.now());

        return promotionRepository.save(promotion);
    }

    public void deletePromotion(Long id) {
        promotionRepository.deleteById(id);
    }

    public Promotion togglePromotionActive(Long id) {
        Promotion promotion = promotionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Promotion not found with id: " + id));
        promotion.setActive(!Boolean.TRUE.equals(promotion.getActive()));
        promotion.setUpdatedAt(LocalDateTime.now());
        return promotionRepository.save(promotion);
    }
}
