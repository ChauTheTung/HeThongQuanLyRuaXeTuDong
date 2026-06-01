package com.autowash.controller;

import com.autowash.dto.LoginDTO;
import com.autowash.dto.RegisterDTO;
import com.autowash.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")

public class AuthController {

    private final AuthService authService;
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterDTO dto) {
        String result = authService.register(dto);
        if (result.equals("Tên đăng nhập đã tồn tại!")) {
            return ResponseEntity.badRequest().body(result);
        }
        return ResponseEntity.ok(result);
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginDTO dto) {
        String result = authService.login(dto);
        if (result.equals("Sai tên đăng nhập hoặc mật khẩu!")) {
            return ResponseEntity.badRequest().body(result);
        }
        return ResponseEntity.ok(result);
    }
}