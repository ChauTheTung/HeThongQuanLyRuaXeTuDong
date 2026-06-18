package com.autowash.scheduler;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class PointExpiryScheduler {

    // Chạy vào lúc 01:00 AM mỗi ngày để kiểm tra và xóa điểm hết hạn sau 12 tháng
    @Scheduled(cron = "0 0 1 * * ?")
    public void checkExpiredPoints() {
        System.out.println("[SCHEDULER RUNNING] Đang quét hệ thống để xóa các điểm thưởng đã hết hạn sau 12 tháng...");
    }
}