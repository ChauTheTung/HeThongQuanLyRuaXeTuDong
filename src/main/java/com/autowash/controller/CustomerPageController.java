package com.autowash.controller;

import com.autowash.entity.Booking;
import com.autowash.entity.Customer;
import com.autowash.entity.Vehicle;
import com.autowash.service.AuthService;
import com.autowash.service.BookingService;
import com.autowash.service.CustomerService;
import com.autowash.service.LoyaltyService;
import com.autowash.service.PromotionService;
import com.autowash.service.VehicleService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/customer")
public class CustomerPageController {

    private final CustomerService customerService;
    private final BookingService bookingService;
    private final VehicleService vehicleService;
    private final LoyaltyService loyaltyService;
    private final PromotionService promotionService;
    private final AuthService authService;

    public CustomerPageController(CustomerService customerService,
                                  BookingService bookingService,
                                  VehicleService vehicleService,
                                  LoyaltyService loyaltyService,
                                  PromotionService promotionService,
                                  AuthService authService) {
        this.customerService = customerService;
        this.bookingService = bookingService;
        this.vehicleService = vehicleService;
        this.loyaltyService = loyaltyService;
        this.promotionService = promotionService;
        this.authService = authService;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model, Authentication authentication) {
        Customer customer = getCurrentCustomer(authentication);
        addCommonProfileAttributes(model, customer);
        return "customer/dashboard";
    }

    @GetMapping("/booking")
    public String booking(Model model,
                          Authentication authentication,
                          @RequestParam(required = false) String status) {
        Customer customer = getCurrentCustomer(authentication);
        model.addAttribute("bookings", status != null && !status.isBlank()
                ? bookingService.getBookingsByCustomerIdAndStatus(customer.getId(), status)
                : bookingService.getBookingsByCustomerId(customer.getId()));
        model.addAttribute("vehicles", vehicleService.getVehiclesByCustomerId(customer.getId()));
        model.addAttribute("newBooking", new Booking());
        model.addAttribute("statusOptions", List.of("PENDING", "CONFIRMED", "COMPLETED", "CANCELLED"));
        model.addAttribute("selectedStatus", status);
        addCommonProfileAttributes(model, customer);
        return "customer/booking";
    }

    @PostMapping("/booking")
    public String createBooking(@ModelAttribute Booking booking, Authentication authentication, RedirectAttributes redirectAttributes) {
        Customer customer = getCurrentCustomer(authentication);
        booking.setCustomerId(customer.getId());
        booking.setStatus("PENDING");
        bookingService.createBooking(booking);
        redirectAttributes.addFlashAttribute("success", "Đặt lịch rửa xe thành công.");
        return "redirect:/customer/booking";
    }

    @PostMapping("/booking/{id}/cancel")
    public String cancelBooking(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        bookingService.updateBookingStatus(id, "CANCELLED");
        redirectAttributes.addFlashAttribute("success", "Đã hủy lịch đặt.");
        return "redirect:/customer/booking";
    }

    @GetMapping("/vehicles")
    public String vehicles(Model model, Authentication authentication) {
        Customer customer = getCurrentCustomer(authentication);
        List<Vehicle> vehicles = vehicleService.getVehiclesByCustomerId(customer.getId());
        model.addAttribute("vehicles", vehicles);
        model.addAttribute("newVehicle", new Vehicle());
        addCommonProfileAttributes(model, customer);
        return "customer/vehicles";
    }

    @PostMapping("/vehicles")
    public String addVehicle(@ModelAttribute Vehicle vehicle, Authentication authentication, RedirectAttributes redirectAttributes) {
        Customer customer = getCurrentCustomer(authentication);
        vehicle.setCustomerId(customer.getId());
        vehicleService.saveVehicle(vehicle);
        redirectAttributes.addFlashAttribute("success", "Thêm xe mới thành công.");
        return "redirect:/customer/vehicles";
    }

    @PostMapping("/vehicles/{id}/delete")
    public String deleteVehicle(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        vehicleService.deleteVehicle(id);
        redirectAttributes.addFlashAttribute("success", "Xóa xe thành công.");
        return "redirect:/customer/vehicles";
    }

    @GetMapping("/history")
    public String history(Model model, Authentication authentication,
                          @RequestParam(required = false) String status) {
        Customer customer = getCurrentCustomer(authentication);
        model.addAttribute("bookings", status != null && !status.isBlank()
                ? bookingService.getBookingsByCustomerIdAndStatus(customer.getId(), status)
                : bookingService.getBookingsByCustomerId(customer.getId()));
        model.addAttribute("statusOptions", List.of("PENDING", "CONFIRMED", "COMPLETED", "CANCELLED"));
        model.addAttribute("selectedStatus", status);
        addCommonProfileAttributes(model, customer);
        return "customer/history";
    }

    @GetMapping("/loyalty")
    public String loyalty(Model model, Authentication authentication) {
        Customer customer = getCurrentCustomer(authentication);
        model.addAttribute("loyalty", loyaltyService.getLoyaltyInfo(customer.getId()));
        model.addAttribute("tiers", loyaltyService.getAllTiers());
        addCommonProfileAttributes(model, customer);
        return "customer/loyalty";
    }

    @PostMapping("/loyalty/redeem")
    public String redeemPoints(@RequestParam int points,
                               @RequestParam(required = false) String description,
                               Authentication authentication,
                               RedirectAttributes redirectAttributes) {
        Customer customer = getCurrentCustomer(authentication);
        try {
            loyaltyService.redeemPoints(customer.getId(), points, description);
            redirectAttributes.addFlashAttribute("success", "Quy đổi điểm thành công.");
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/customer/loyalty";
    }

    @GetMapping("/promotions")
    public String promotions(Model model, Authentication authentication) {
        Customer customer = getCurrentCustomer(authentication);
        model.addAttribute("promotions", promotionService.getActivePromotions());
        addCommonProfileAttributes(model, customer);
        return "customer/promotions";
    }

    @GetMapping("/notifications")
    public String notifications(Model model, Authentication authentication) {
        Customer customer = getCurrentCustomer(authentication);
        addCommonProfileAttributes(model, customer);
        return "customer/notifications";
    }

    @GetMapping("/profile")
    public String profile(Model model, Authentication authentication) {
        Customer customer = getCurrentCustomer(authentication);
        addCommonProfileAttributes(model, customer);
        return "customer/profile";
    }

    @PostMapping("/profile")
    public String updateProfile(@ModelAttribute Customer profile, Authentication authentication, RedirectAttributes redirectAttributes) {
        Customer current = getCurrentCustomer(authentication);
        customerService.updateProfile(current.getId(), profile);
        redirectAttributes.addFlashAttribute("success", "Cập nhật hồ sơ thành công.");
        return "redirect:/customer/profile";
    }

    @PostMapping("/profile/password")
    public String updatePassword(@RequestParam String currentPassword,
                                 @RequestParam String newPassword,
                                 @RequestParam String confirmPassword,
                                 Authentication authentication,
                                 RedirectAttributes redirectAttributes) {
        if (!newPassword.equals(confirmPassword)) {
            redirectAttributes.addFlashAttribute("error", "Mật khẩu xác nhận không khớp.");
            return "redirect:/customer/profile";
        }
        authService.changePassword(authentication.getName(), currentPassword, newPassword);
        redirectAttributes.addFlashAttribute("success", "Đổi mật khẩu thành công.");
        return "redirect:/customer/profile";
    }

    @PostMapping("/profile/notifications")
    public String saveNotificationSettings(RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("success", "Cài đặt thông báo đã được lưu.");
        return "redirect:/customer/profile";
    }

    private Customer getCurrentCustomer(Authentication authentication) {
        return customerService.getCustomerByUsername(authentication.getName());
    }

    private void addCommonProfileAttributes(Model model, Customer customer) {
        model.addAttribute("profile", customer);
        model.addAttribute("loyaltyPoints", customer.getLoyaltyPoints());
        model.addAttribute("memberTier", loyaltyService.getLoyaltyInfo(customer.getId()).getCurrentTier());
        model.addAttribute("notifCount", 3);
        model.addAttribute("vehicleCount", vehicleService.getVehiclesByCustomerId(customer.getId()).size());
        model.addAttribute("totalWashes", bookingService.getBookingsByCustomerId(customer.getId()).size());
    }
}
