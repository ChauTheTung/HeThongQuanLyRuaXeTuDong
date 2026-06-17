package com.autowash.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class SchedulerService {

    private static final Logger logger = LoggerFactory.getLogger(SchedulerService.class);

    private final LoyaltyService loyaltyService;

    public SchedulerService(LoyaltyService loyaltyService) {
        this.loyaltyService = loyaltyService;
    }

    @Scheduled(cron = "0 0 1 1 * ?")
    public void runMonthlyReview() {
        logger.info("Running monthly loyalty tier review");
        loyaltyService.expireOldPoints();
        logger.info("Monthly loyalty tier review completed");
    }

    @Scheduled(cron = "0 30 1 1 * ?")
    public void runPointExpiration() {
        logger.info("Running monthly point expiration");
        loyaltyService.expireOldPoints();
        logger.info("Point expiration process completed");
    }
}
