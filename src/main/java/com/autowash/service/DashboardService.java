package com.autowash.service;

import com.autowash.entity.Booking;
import com.autowash.repository.BookingRepository;
import com.autowash.repository.CustomerRepository;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class DashboardService {

    private final BookingRepository bookingRepository;
    private final CustomerRepository customerRepository;

    public DashboardService(BookingRepository bookingRepository, CustomerRepository customerRepository) {
        this.bookingRepository = bookingRepository;
        this.customerRepository = customerRepository;
    }

    public Map<String, Object> getRevenueSummary() {
        double totalRevenue = bookingRepository.findAll().stream()
                .mapToDouble(booking -> booking.getTotalPrice() != null ? booking.getTotalPrice() : 0.0)
                .sum();

        long completedBookings = bookingRepository.findByStatus("COMPLETED").size();
        long pendingBookings = bookingRepository.findByStatus("PENDING").size();

        Map<String, Object> summary = new HashMap<>();
        summary.put("totalRevenue", totalRevenue);
        summary.put("completedBookings", completedBookings);
        summary.put("pendingBookings", pendingBookings);
        summary.put("totalCustomers", customerRepository.count());
        return summary;
    }

    public Map<String, Long> getBookingStatistics() {
        List<Booking> allBookings = bookingRepository.findAll();
        Map<String, Long> stats = allBookings.stream()
                .collect(Collectors.groupingBy(Booking::getStatus, Collectors.counting()));
        return stats;
    }

    public Map<String, Object> getCustomerStatistics() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalCustomers", customerRepository.count());
        stats.put("activeCustomers", customerRepository.count());
        return stats;
    }
}
