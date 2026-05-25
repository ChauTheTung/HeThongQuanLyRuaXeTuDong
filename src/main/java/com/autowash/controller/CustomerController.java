package com.autowash.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/customer")
public class CustomerController {

    @GetMapping("/dashboard")
    public String dashboard() {
        return "customer/dashboard";
    }

    @GetMapping("/profile")
    public String profile(Model model) {
        model.addAttribute("memberTier", "Gold Member");
        model.addAttribute("loyaltyPoints", "1.200");
        model.addAttribute("memberSince", "03/2024");
        model.addAttribute("totalWashes", "28");
        model.addAttribute("vehicleCount", "2");
        model.addAttribute("notificationCount", 3);
        return "customer/profile";
    }

    @GetMapping({"/booking", "/vehicles", "/history", "/loyalty", "/promotions", "/notifications", "/search"})
    public String placeholder() {
        return "redirect:/customer/dashboard";
    }
}
