package com.autowash.controller;

import com.autowash.entity.Booking;
import com.autowash.entity.Customer;
import com.autowash.entity.LoyaltyTier;
import com.autowash.entity.Promotion;
import com.autowash.entity.ServicePricing;
import com.autowash.service.BookingService;
import com.autowash.service.CustomerService;
import com.autowash.service.DashboardService;
import com.autowash.service.LoyaltyService;
import com.autowash.service.PromotionService;
import com.autowash.service.PricingService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin")
public class AdminPageController {

    private final DashboardService dashboardService;
    private final BookingService bookingService;
    private final CustomerService customerService;
    private final PromotionService promotionService;
    private final LoyaltyService loyaltyService;
    private final PricingService pricingService;

    public AdminPageController(DashboardService dashboardService,
                               BookingService bookingService,
                               CustomerService customerService,
                               PromotionService promotionService,
                               LoyaltyService loyaltyService,
                               PricingService pricingService) {
        this.dashboardService = dashboardService;
        this.bookingService = bookingService;
        this.customerService = customerService;
        this.promotionService = promotionService;
        this.loyaltyService = loyaltyService;
        this.pricingService = pricingService;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("summary", dashboardService.getRevenueSummary());
        model.addAttribute("bookingStats", dashboardService.getBookingStatistics());
        model.addAttribute("customerStats", dashboardService.getCustomerStatistics());
        return "admin/dashboard";
    }

    @GetMapping("/bookings")
    public String bookings(Model model,
                           @RequestParam(required = false) String customerQuery,
                           @RequestParam(required = false) String status) {
        List<Booking> bookings;
        if (customerQuery != null && !customerQuery.isBlank()) {
            List<Customer> matchedCustomers = customerService.searchCustomers(customerQuery);
            List<Long> ids = matchedCustomers.stream()
                    .map(Customer::getId)
                    .collect(Collectors.toList());
            if (ids.isEmpty()) {
                bookings = List.of();
            } else if (status != null && !status.isBlank()) {
                bookings = bookingService.getBookingsByCustomerIdsAndStatus(ids, status);
            } else {
                bookings = bookingService.getBookingsByCustomerIds(ids);
            }
        } else if (status != null && !status.isBlank()) {
            bookings = bookingService.getBookingsByStatus(status);
        } else {
            bookings = bookingService.getAllBookings();
        }

        Map<Long, String> customerNames = bookings.stream()
                .map(Booking::getCustomerId)
                .distinct()
                .collect(Collectors.toMap(
                        id -> id, 
                        customerId -> {
                            try {
                                Customer c = customerService.getCustomerById(customerId);
                                return c.getFullName();
                            } catch (Exception e) {
                                return "Khách ẩn danh";
                            }
                        }
                ));

        model.addAttribute("bookings", bookings);
        model.addAttribute("customerNames", customerNames);
        model.addAttribute("statusOptions", List.of("PENDING", "CONFIRMED", "COMPLETED", "CANCELLED"));
        model.addAttribute("selectedStatus", status);
        model.addAttribute("customerQuery", customerQuery);
        return "admin/bookings";
    }

    @PostMapping("/bookings/{id}/status")
    public String updateBookingStatus(@PathVariable Long id,
                                      @RequestParam String status,
                                      RedirectAttributes redirectAttributes) {
        bookingService.updateBookingStatus(id, status);
        redirectAttributes.addFlashAttribute("success", "Cập nhật trạng thái đặt chỗ thành công.");
        return "redirect:/admin/bookings";
    }

    @PostMapping("/bookings/{id}/delete")
    public String deleteBooking(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        bookingService.deleteBooking(id);
        redirectAttributes.addFlashAttribute("success", "Đã xóa lịch đặt #" + id + " thành công.");
        return "redirect:/admin/bookings";
    }

    @GetMapping("/customers")
    public String customers(Model model,
                            @RequestParam(required = false) String q) {
        List<Customer> customers = (q != null && !q.isBlank())
                ? customerService.searchCustomers(q)
                : customerService.getAllCustomers();
        model.addAttribute("customers", customers);
        model.addAttribute("query", q);
        return "admin/customers";
    }

    @GetMapping("/customers/{id}/edit")
    public String editCustomer(@PathVariable Long id, Model model) {
        Customer customer = customerService.getCustomerById(id);
        model.addAttribute("customer", customer);
        return "admin/customer-edit";
    }

    @PostMapping("/customers/{id}")
    public String updateCustomer(@PathVariable Long id, Customer customer, RedirectAttributes redirectAttributes) {
        customerService.updateProfile(id, customer);
        redirectAttributes.addFlashAttribute("success", "Cập nhật khách hàng thành công.");
        return "redirect:/admin/customers";
    }

    @PostMapping("/customers/{id}/delete")
    public String deleteCustomer(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        customerService.deleteCustomer(id);
        redirectAttributes.addFlashAttribute("success", "Xóa khách hàng thành công.");
        return "redirect:/admin/customers";
    }

    @GetMapping("/loyalty")
    public String loyalty(Model model) {
        List<LoyaltyTier> tiers = loyaltyService.getAllTiers();
        model.addAttribute("tiers", tiers);
        model.addAttribute("newTier", new LoyaltyTier());
        return "admin/loyalty";
    }

    @PostMapping("/loyalty")
    public String createTier(LoyaltyTier tier, RedirectAttributes redirectAttributes) {
        loyaltyService.createTier(tier);
        redirectAttributes.addFlashAttribute("success", "Tạo hạng loyalty thành công.");
        return "redirect:/admin/loyalty";
    }

    @PostMapping("/loyalty/{id}/delete")
    public String deleteTier(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        loyaltyService.deleteTier(id);
        redirectAttributes.addFlashAttribute("success", "Xóa hạng loyalty thành công.");
        return "redirect:/admin/loyalty";
    }

    @GetMapping("/promotions")
    public String promotions(Model model) {
        List<Promotion> promotions = promotionService.getAllPromotions();
        model.addAttribute("promotions", promotions);
        model.addAttribute("newPromotion", new Promotion());
        return "admin/promotions";
    }

    @PostMapping("/promotions")
    public String createPromotion(Promotion promotion, RedirectAttributes redirectAttributes) {
        promotionService.createPromotion(promotion);
        redirectAttributes.addFlashAttribute("success", "Tạo khuyến mãi thành công.");
        return "redirect:/admin/promotions";
    }

    @PostMapping("/promotions/{id}/toggle")
    public String togglePromotion(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        promotionService.togglePromotionActive(id);
        redirectAttributes.addFlashAttribute("success", "Đổi trạng thái khuyến mãi thành công.");
        return "redirect:/admin/promotions";
    }

    @PostMapping("/promotions/{id}/delete")
    public String deletePromotion(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        promotionService.deletePromotion(id);
        redirectAttributes.addFlashAttribute("success", "Xóa khuyến mãi thành công.");
        return "redirect:/admin/promotions";
    }

    @GetMapping("/reports")
    public String reports(Model model) {
        model.addAttribute("summary", dashboardService.getRevenueSummary());
        model.addAttribute("bookingStats", dashboardService.getBookingStatistics());
        return "admin/reports";
    }

    @GetMapping("/services")
    public String services(Model model) {
        model.addAttribute("services", pricingService.getAllServices());
        model.addAttribute("newService", new ServicePricing());
        return "admin/services";
    }

    @PostMapping("/services")
    public String saveService(@ModelAttribute ServicePricing service, RedirectAttributes redirectAttributes) {
        pricingService.saveService(service);
        redirectAttributes.addFlashAttribute("success", "Lưu dịch vụ thành công.");
        return "redirect:/admin/services";
    }

    @PostMapping("/services/{id}/delete")
    public String deleteService(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        pricingService.deleteService(id);
        redirectAttributes.addFlashAttribute("success", "Xóa dịch vụ thành công.");
        return "redirect:/admin/services";
    }
}
