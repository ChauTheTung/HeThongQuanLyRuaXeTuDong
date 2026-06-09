package com.autowash.controller;

import com.autowash.dto.LoginDTO;
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
            @RequestParam("email") String email,
            @RequestParam("password") String password,
            RedirectAttributes redirectAttributes
    ) {
        RegisterDTO dto = new RegisterDTO();
        dto.setUsername(email);
        dto.setPassword(password);

        String result = authService.register(dto);
        if (result.equals("Tên đăng nhập đã tồn tại!")) {
            redirectAttributes.addFlashAttribute("error", result);
            return "redirect:/register";
        }

        redirectAttributes.addFlashAttribute("success", result);
        return "redirect:/login";
    }

    @PostMapping("/login")
    public String login(
            @RequestParam("username") String username,
            @RequestParam("password") String password,
            RedirectAttributes redirectAttributes
    ) {
        LoginDTO dto = new LoginDTO();
        dto.setUsername(username);
        dto.setPassword(password);

        String result = authService.login(dto);
        if (result.equals("Sai tên đăng nhập hoặc mật khẩu!")) {
            redirectAttributes.addFlashAttribute("error", result);
            return "redirect:/login";
        }

        redirectAttributes.addFlashAttribute("success", result);
        return "redirect:/customer/dashboard";
    }
}
