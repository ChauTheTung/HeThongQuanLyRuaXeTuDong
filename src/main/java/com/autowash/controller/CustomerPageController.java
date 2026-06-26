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
import com.autowash.service.PricingService;
import com.autowash.service.NotificationService;
import com.autowash.dto.NotificationDTO;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import com.autowash.entity.Promotion;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


@Controller
@RequestMapping("/customer")
public class CustomerPageController {

    private final CustomerService customerService;
    private final BookingService bookingService;
    private final VehicleService vehicleService;
    private final LoyaltyService loyaltyService;
    private final PromotionService promotionService;
    private final AuthService authService;
    private final PricingService pricingService;
    private final NotificationService notificationService;

    public CustomerPageController(CustomerService customerService,
                                  BookingService bookingService,
                                  VehicleService vehicleService,
                                  LoyaltyService loyaltyService,
                                  PromotionService promotionService,
                                  AuthService authService,
                                  PricingService pricingService,
                                  NotificationService notificationService) {
        this.customerService = customerService;
        this.bookingService = bookingService;
        this.vehicleService = vehicleService;
        this.loyaltyService = loyaltyService;
        this.promotionService = promotionService;
        this.authService = authService;
        this.pricingService = pricingService;
        this.notificationService = notificationService;
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
        String tier = loyaltyService.getLoyaltyInfo(customer.getId()).getCurrentTier();
        int minBookingDays = getBookingAdvanceDays(tier);

        model.addAttribute("bookings", status != null && !status.isBlank()
                ? bookingService.getBookingsByCustomerIdAndStatus(customer.getId(), status)
                : bookingService.getBookingsByCustomerId(customer.getId()));
        List<Vehicle> vehicles = vehicleService.getVehiclesByCustomerId(customer.getId());
        model.addAttribute("vehicles", vehicles);
        Map<Long, Vehicle> vehiclesMap = vehicles.stream().collect(Collectors.toMap(Vehicle::getId, v -> v));
        model.addAttribute("vehiclesMap", vehiclesMap);
        model.addAttribute("servicesList", pricingService.getAllServices());
        model.addAttribute("newBooking", new Booking());
        model.addAttribute("statusOptions", List.of("PENDING", "CONFIRMED", "COMPLETED", "CANCELLED"));
        model.addAttribute("selectedStatus", status);
        model.addAttribute("minBookingDays", minBookingDays);
        addCommonProfileAttributes(model, customer);
        return "customer/booking";
    }

    @PostMapping("/booking")
    public String createBooking(@ModelAttribute Booking booking, Authentication authentication, RedirectAttributes redirectAttributes) {
        Customer customer = getCurrentCustomer(authentication);
        booking.setCustomerId(customer.getId());
        booking.setStatus("PENDING");

        int minBookingDays = getBookingAdvanceDays(loyaltyService.getLoyaltyInfo(customer.getId()).getCurrentTier());
        if (booking.getBookingTime() == null) {
            redirectAttributes.addFlashAttribute("error", "Vui lòng chọn thời gian hẹn.");
            return "redirect:/customer/booking";
        }

        LocalDate minAllowedDate = LocalDate.now().plusDays(minBookingDays);
        if (booking.getBookingTime().toLocalDate().isBefore(minAllowedDate)) {
            redirectAttributes.addFlashAttribute("error", "Hạng " + loyaltyService.getLoyaltyInfo(customer.getId()).getCurrentTier() + " chỉ được đặt lịch trước tối thiểu " + minBookingDays + " ngày.");
            return "redirect:/customer/booking";
        }

        if (booking.getVehicleId() != null && booking.getServiceName() != null) {
            Vehicle vehicle = vehicleService.getVehiclesByCustomerId(customer.getId()).stream()
                    .filter(v -> v.getId().equals(booking.getVehicleId()))
                    .findFirst().orElse(null);
            if (vehicle != null) {
                var serviceOpt = pricingService.getServicesByVehicleType(vehicle.getVehicleType()).stream()
                        .filter(s -> s.getServiceName().equals(booking.getServiceName()))
                        .findFirst();
                if (serviceOpt.isPresent()) {
                    var service = serviceOpt.get();
                    booking.setServicePrice(service.getPrice());
                    double discountPercent = 0.0;

                    if (booking.getPromotionCode() != null && !booking.getPromotionCode().isBlank()) {
                        Promotion promo = promotionService.getActivePromotionByName(booking.getPromotionCode());
                        if (promo == null) {
                            redirectAttributes.addFlashAttribute("error", "Mã khuyến mãi không hợp lệ hoặc đã hết hạn.");
                            return "redirect:/customer/booking";
                        }
                        String currentTier = loyaltyService.getLoyaltyInfo(customer.getId()).getCurrentTier();
                        if (promo.getTierName() != null && !promo.getTierName().isBlank() && !promo.getTierName().equalsIgnoreCase(currentTier)) {
                            redirectAttributes.addFlashAttribute("error", "Mã khuyến mãi không phù hợp với hạng của bạn.");
                            return "redirect:/customer/booking";
                        }
                        discountPercent += promo.getDiscountPercent() != null ? promo.getDiscountPercent() : 0.0;
                    }

                    if (booking.getUsedLoyaltyPoints() != null && booking.getUsedLoyaltyPoints() > 0) {
                        int points = booking.getUsedLoyaltyPoints();
                        if (points % 200 != 0) {
                            redirectAttributes.addFlashAttribute("error", "Số điểm sử dụng phải là bội số của 200.");
                            return "redirect:/customer/booking";
                        }
                        if (customer.getRedeemablePoints() < points) {
                            redirectAttributes.addFlashAttribute("error", "Bạn không có đủ điểm để sử dụng.");
                            return "redirect:/customer/booking";
                        }
                        double loyaltyDiscount = 5.0 * (points / 200);
                        discountPercent += loyaltyDiscount;
                        loyaltyService.redeemPoints(customer.getId(), points, "Sử dụng điểm để được giảm giá đặt lịch");
                    }

                    booking.setDiscountPercent(discountPercent);
                    double price = service.getPrice();
                    booking.setTotalPrice(price * Math.max(0.0, (100.0 - discountPercent) / 100.0));
                }
            }
        }

        if (booking.getTotalPrice() == null || booking.getTotalPrice() <= 0) {
            redirectAttributes.addFlashAttribute("error", "Không thể tính toán giá đặt lịch. Vui lòng kiểm tra thông tin.");
            return "redirect:/customer/booking";
        }

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
        try {
            vehicleService.saveVehicle(vehicle);
            redirectAttributes.addFlashAttribute("success", "Thêm xe mới thành công.");
        } catch (org.springframework.dao.DataIntegrityViolationException e) {
            redirectAttributes.addFlashAttribute("error", "Biển số xe này đã được đăng ký trên hệ thống!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Có lỗi xảy ra khi thêm xe. Vui lòng thử lại.");
        }
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
        List<Booking> bookings = status != null && !status.isBlank()
            ? bookingService.getBookingsByCustomerIdAndStatus(customer.getId(), status)
            : bookingService.getBookingsByCustomerId(customer.getId());
        model.addAttribute("bookings", bookings);
        List<Vehicle> vehicles = vehicleService.getVehiclesByCustomerId(customer.getId());
        Map<Long, Vehicle> vehiclesMap = vehicles.stream().collect(Collectors.toMap(Vehicle::getId, v -> v));
        model.addAttribute("vehiclesMap", vehiclesMap);
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

    private int getBookingAdvanceDays(String tierName) {
        if (tierName == null) {
            return 7;
        }
        return switch (tierName.toUpperCase()) {
            case "GOLD" -> 2;
            case "SILVER" -> 5;
            default -> 7;
        };
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
        List<NotificationDTO> notifs = notificationService.getNotificationsForCustomer(customer.getId());
        model.addAttribute("notifications", notifs);
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
        model.addAttribute("redeemablePoints", customer.getRedeemablePoints());
        model.addAttribute("memberTier", loyaltyService.getLoyaltyInfo(customer.getId()).getCurrentTier());
        List<NotificationDTO> notifs = notificationService.getNotificationsForCustomer(customer.getId());
        model.addAttribute("notifCount", notifs.size() > 5 ? 5 : notifs.size());
        model.addAttribute("vehicleCount", vehicleService.getVehiclesByCustomerId(customer.getId()).size());
        model.addAttribute("totalWashes", bookingService.getBookingsByCustomerId(customer.getId()).size());
    }
}
