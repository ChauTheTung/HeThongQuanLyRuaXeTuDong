package com.autowash.controller;

import com.autowash.dto.RegisterDTO;
import com.autowash.service.AuthService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping("/")
    public String home() {
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String login() {
        return "auth/login";
    }

    @GetMapping("/register")
    public String register() {
        return "auth/register";
    }

    @GetMapping("/forgot-password")
    public String forgotPassword() {
        return "redirect:/login";
    }

    @PostMapping("/register")
    public String register(
            @RequestParam("fullName") String fullName,
            @RequestParam("email") String email,
            @RequestParam("phoneNumber") String phoneNumber,
            @RequestParam("password") String password,
            RedirectAttributes redirectAttributes
    ) {
        RegisterDTO dto = new RegisterDTO();
        dto.setFullName(fullName);
        dto.setUsername(email);
        dto.setEmail(email);
        dto.setPassword(password);
        dto.setPhoneNumber(phoneNumber);

        // require @gmail.com addresses only
        if (email == null || !email.toLowerCase().endsWith("@gmail.com")) {
            redirectAttributes.addFlashAttribute("error", "Vui lòng sử dụng email @gmail.com để đăng ký.");
            return "redirect:/register";
        }

        String result = authService.register(dto);
        if (result.equals("Tên đăng nhập đã tồn tại!")) {
            redirectAttributes.addFlashAttribute("error", result);
            return "redirect:/register";
        }

        redirectAttributes.addFlashAttribute("success", result);
        return "redirect:/login";
    }
}
