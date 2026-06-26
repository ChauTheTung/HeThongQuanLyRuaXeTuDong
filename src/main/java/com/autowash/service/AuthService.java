package com.autowash.service;

import com.autowash.dto.LoginDTO;
import com.autowash.dto.RegisterDTO;
import com.autowash.entity.Customer;
import com.autowash.entity.User;
import com.autowash.repository.CustomerRepository;
import com.autowash.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository, CustomerRepository customerRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.customerRepository = customerRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public String register(RegisterDTO dto) {
        // Form gửi 'email', dùng email làm username đăng nhập
        String usernameToUse = (dto.getUsername() != null && !dto.getUsername().isBlank())
                ? dto.getUsername()
                : dto.getEmail();

        if (usernameToUse == null || usernameToUse.isBlank()) {
            return "Vui lòng nhập email hoặc tên đăng nhập!";
        }

        if (userRepository.findByUsername(usernameToUse).isPresent()) {
            return "Tên đăng nhập đã tồn tại!";
        }

        User newUser = new User();
        newUser.setUsername(usernameToUse);
        newUser.setPassword(passwordEncoder.encode(dto.getPassword()));
        newUser.setRole("ROLE_CUSTOMER");

        User savedUser = userRepository.save(newUser);

        Customer newCustomer = new Customer();
        newCustomer.setUserId(savedUser.getId());
        newCustomer.setFullName(dto.getFullName() != null ? dto.getFullName() : usernameToUse);
        newCustomer.setEmail(dto.getEmail() != null ? dto.getEmail() : usernameToUse);
        newCustomer.setPhoneNumber(dto.getPhoneNumber());
            newCustomer.setLoyaltyPoints(0);
            newCustomer.setRedeemablePoints(0);
        newCustomer.setCreatedAt(LocalDateTime.now());
        newCustomer.setUpdatedAt(LocalDateTime.now());

        customerRepository.save(newCustomer);
        return "Đăng ký thành công!";
    }

    public String login(LoginDTO dto) {
        Optional<User> userOpt = userRepository.findByUsername(dto.getUsername());

        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if (passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
                return "Đăng nhập thành công! Chào mừng " + user.getUsername();
            }
        }
        return "Sai tên đăng nhập hoặc mật khẩu!";
    }

    public void changePassword(String username, String currentPassword, String newPassword) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng: " + username));
        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new IllegalArgumentException("Mật khẩu hiện tại không đúng.");
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }
}