package com.autowash.scheduler;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class TierScheduler {

    // Chạy vào lúc 00:00:00 ngày mùng 1 hàng tháng để xét lại hạng thành viên
    @Scheduled(cron = "0 0 0 1 * ?")
    public void reviewMonthlyTiers() {
        System.out.println("[SCHEDULER RUNNING] Đang tự động quét và cập nhật lại hạng thành viên hàng tháng...");
    }
}
