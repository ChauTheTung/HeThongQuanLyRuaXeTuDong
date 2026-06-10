package com.autowash.service;

import com.autowash.dto.LoginDTO;
import com.autowash.dto.RegisterDTO;
import com.autowash.entity.User;
import com.autowash.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    
    // Constructor Injection: Spring sẽ tự động cung cấp UserRepository và PasswordEncoder khi tạo AuthService
    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // Logic Đăng Ký
    public String register(RegisterDTO dto) {
        if (userRepository.findByUsername(dto.getUsername()).isPresent()) {
            return "Tên đăng nhập đã tồn tại!";
        }

        User newUser = new User();
        newUser.setUsername(dto.getUsername());
        newUser.setPassword(passwordEncoder.encode(dto.getPassword()));
        newUser.setRole("ROLE_CUSTOMER");


        userRepository.save(newUser);
        return "Đăng ký thành công!";
    }

    // Logic Đăng Nhập
    public String login(LoginDTO dto) {
        Optional<User> userOpt = userRepository.findByUsername(dto.getUsername());
        
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            // So sánh mật khẩu do người dùng nhập với mật khẩu đã mã hóa trong DB
            if (passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
                return "Đăng nhập thành công! Chào mừng " + user.getUsername();
            }
        }
        return "Sai tên đăng nhập hoặc mật khẩu!";
    }
}