package com.autowash.service;

import com.autowash.entity.Booking;
import com.autowash.repository.BookingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class BookingService {

    @Autowired
    private BookingRepository bookingRepository;

    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }

    public Booking createBooking(Booking booking) {
        // Mặc định khi tạo mới lịch đặt, trạng thái sẽ là PENDING
        booking.setStatus("PENDING");
        booking.setCreatedAt(LocalDateTime.now());
        booking.setUpdatedAt(LocalDateTime.now());
        return bookingRepository.save(booking);
    }

    public Booking updateBookingStatus(Long id, String status) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Booking not found with id: " + id));
        booking.setStatus(status);
        booking.setUpdatedAt(LocalDateTime.now());
        return bookingRepository.save(booking);
    }

    public Booking updateBooking(Long id, Booking bookingDetails) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Booking not found with id: " + id));
        booking.setBookingTime(bookingDetails.getBookingTime());
        booking.setTotalPrice(bookingDetails.getTotalPrice());
        booking.setStatus(bookingDetails.getStatus() != null ? bookingDetails.getStatus() : booking.getStatus());
        booking.setUpdatedAt(LocalDateTime.now());
        return bookingRepository.save(booking);
    }

    public List<Booking> getBookingsByStatus(String status) {
        return bookingRepository.findByStatus(status);
    }

    public List<Booking> getBookingsByCustomerId(Long customerId) {
        return bookingRepository.findByCustomerId(customerId);
    }

    public List<Booking> getBookingsByCustomerIdAndStatus(Long customerId, String status) {
        return bookingRepository.findByCustomerIdAndStatus(customerId, status);
    }

    public List<Booking> getBookingsByCustomerIds(List<Long> customerIds) {
        return bookingRepository.findByCustomerIdIn(customerIds);
    }

    public List<Booking> getBookingsByCustomerIdsAndStatus(List<Long> customerIds, String status) {
        return bookingRepository.findByCustomerIdInAndStatus(customerIds, status);
    }

    public void deleteBooking(Long id) {
        bookingRepository.deleteById(id);
    }
}