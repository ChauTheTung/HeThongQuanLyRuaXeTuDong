package com.autowash.service;

import com.autowash.dto.NotificationDTO;
import com.autowash.entity.Booking;
import com.autowash.entity.PointTransaction;
import com.autowash.entity.Vehicle;
import com.autowash.repository.BookingRepository;
import com.autowash.repository.PointTransactionRepository;
import com.autowash.repository.VehicleRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class NotificationService {

    private final BookingRepository bookingRepository;
    private final PointTransactionRepository pointTransactionRepository;
    private final VehicleRepository vehicleRepository;

    public NotificationService(BookingRepository bookingRepository,
                               PointTransactionRepository pointTransactionRepository,
                               VehicleRepository vehicleRepository) {
        this.bookingRepository = bookingRepository;
        this.pointTransactionRepository = pointTransactionRepository;
        this.vehicleRepository = vehicleRepository;
    }

    public List<NotificationDTO> getNotificationsForCustomer(Long customerId) {
        List<NotificationDTO> notifications = new ArrayList<>();

        // 1. Lấy thông báo từ Bookings
        List<Booking> bookings = bookingRepository.findByCustomerId(customerId);
        for (Booking booking : bookings) {
            String licensePlate = "xe đã chọn";
            Vehicle vehicle = vehicleRepository.findById(booking.getVehicleId()).orElse(null);
            if (vehicle != null) {
                licensePlate = vehicle.getLicensePlate();
            }

            // A. Thông báo tạo lịch đặt (luôn có)
            NotificationDTO createNotif = new NotificationDTO();
            createNotif.setTitle("Đặt lịch rửa xe thành công");
            createNotif.setContent(String.format("Bạn đã gửi yêu cầu đặt lịch rửa xe cho xe %s (%s) vào lúc %s.",
                    licensePlate,
                    booking.getServiceName() != null ? booking.getServiceName() : "Dịch vụ",
                    booking.getBookingTime().toString().replace("T", " ")));
            createNotif.setTime(booking.getCreatedAt());
            createNotif.setType("info");
            createNotif.setIcon("bi-calendar-check");
            notifications.add(createNotif);

            // B. Thông báo trạng thái booking thay đổi
            if ("CONFIRMED".equals(booking.getStatus())) {
                NotificationDTO confirmNotif = new NotificationDTO();
                confirmNotif.setTitle("Lịch đặt đã được xác nhận");
                confirmNotif.setContent(String.format("Yêu cầu đặt lịch rửa xe %s vào %s của bạn đã được Admin xác nhận.",
                        licensePlate, booking.getBookingTime().toString().replace("T", " ")));
                confirmNotif.setTime(booking.getUpdatedAt());
                confirmNotif.setType("success");
                confirmNotif.setIcon("bi-check-circle-fill");
                notifications.add(confirmNotif);
            } else if ("COMPLETED".equals(booking.getStatus())) {
                NotificationDTO completeNotif = new NotificationDTO();
                completeNotif.setTitle("Dịch vụ rửa xe hoàn thành");
                completeNotif.setContent(String.format("Lịch rửa xe %s vào %s đã hoàn thành xuất sắc. Cảm ơn quý khách!",
                        licensePlate, booking.getBookingTime().toString().replace("T", " ")));
                completeNotif.setTime(booking.getUpdatedAt());
                completeNotif.setType("success");
                completeNotif.setIcon("bi-emoji-smile-fill");
                notifications.add(completeNotif);
            } else if ("CANCELLED".equals(booking.getStatus())) {
                NotificationDTO cancelNotif = new NotificationDTO();
                cancelNotif.setTitle("Lịch đặt đã bị hủy");
                cancelNotif.setContent(String.format("Lịch đặt rửa xe %s vào %s của bạn đã bị hủy bỏ.",
                        licensePlate, booking.getBookingTime().toString().replace("T", " ")));
                cancelNotif.setTime(booking.getUpdatedAt());
                cancelNotif.setType("danger");
                cancelNotif.setIcon("bi-x-circle-fill");
                notifications.add(cancelNotif);
            }
        }

        // 2. Lấy thông báo từ Point Transactions
        List<PointTransaction> transactions = pointTransactionRepository.findByCustomerId(customerId);
        for (PointTransaction pt : transactions) {
            NotificationDTO ptNotif = new NotificationDTO();
            if (pt.getPoints() > 0) {
                ptNotif.setTitle("Nhận điểm tích lũy");
                ptNotif.setContent(String.format("Tài khoản của bạn được cộng +%d điểm thưởng từ hoạt động: %s.",
                        pt.getPoints(), pt.getDescription()));
                ptNotif.setType("success");
                ptNotif.setIcon("bi-gift-fill");
            } else {
                ptNotif.setTitle("Quy đổi điểm thưởng");
                ptNotif.setContent(String.format("Tài khoản của bạn đã trừ %d điểm thưởng để: %s.",
                        Math.abs(pt.getPoints()), pt.getDescription()));
                ptNotif.setType("warning");
                ptNotif.setIcon("bi-trophy-fill");
            }
            ptNotif.setTime(pt.getCreatedAt() != null ? pt.getCreatedAt() : LocalDateTime.now());
            notifications.add(ptNotif);
        }

        // 3. Đăng ký tài khoản (Thông báo chào mừng nếu có ngày tạo customer)
        // (Có thể bỏ qua hoặc tạo 1 cái mặc định ở dưới cùng để trang không bao giờ trống nếu mới đăng ký)
        if (notifications.isEmpty()) {
            NotificationDTO welcomeNotif = new NotificationDTO();
            welcomeNotif.setTitle("Chào mừng thành viên mới");
            welcomeNotif.setContent("Chào mừng bạn đã tham gia hệ thống rửa xe tự động AutoWash Pro! Hãy đăng ký phương tiện và đặt lịch ngay nhé.");
            welcomeNotif.setTime(LocalDateTime.now().minusHours(1));
            welcomeNotif.setType("info");
            welcomeNotif.setIcon("bi-stars");
            notifications.add(welcomeNotif);
        }

        // Sắp xếp các thông báo giảm dần theo thời gian (mới nhất lên đầu)
        notifications.sort(Comparator.comparing(NotificationDTO::getTime).reversed());

        return notifications;
    }
}
