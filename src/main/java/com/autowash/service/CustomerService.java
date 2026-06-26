package com.autowash.service;

import com.autowash.entity.Customer;
import com.autowash.entity.User;
import com.autowash.exception.ResourceNotFoundException;
import com.autowash.repository.CustomerRepository;
import com.autowash.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final UserRepository userRepository;

    public CustomerService(CustomerRepository customerRepository, UserRepository userRepository) {
        this.customerRepository = customerRepository;
        this.userRepository = userRepository;
    }

    public Customer getCustomerById(Long id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy khách hàng với ID: " + id));
    }

    public Customer getCustomerByUserId(Long userId) {
        List<Customer> customers = customerRepository.findByUserId(userId);
        if (customers.isEmpty()) {
            throw new ResourceNotFoundException("Không tìm thấy khách hàng liên kết với User ID: " + userId);
        }
        return customers.get(customers.size() - 1);
    }

    public Customer getCustomerByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng: " + username));
        List<Customer> customers = customerRepository.findByUserId(user.getId());
        if (!customers.isEmpty()) {
            return customers.get(customers.size() - 1);
        } else {
            // Tự động tạo customer record nếu chưa có (fix cho tài khoản cũ bị lỗi)
            Customer newCustomer = new Customer();
            newCustomer.setUserId(user.getId());
            newCustomer.setFullName(user.getUsername());
            newCustomer.setEmail(user.getUsername());
            newCustomer.setLoyaltyPoints(0);
            newCustomer.setRedeemablePoints(0);
            newCustomer.setCreatedAt(java.time.LocalDateTime.now());
            newCustomer.setUpdatedAt(java.time.LocalDateTime.now());
            return customerRepository.save(newCustomer);
        }
    }

    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }

    public Customer updateProfile(Long id, Customer updatedData) {
        Customer existingCustomer = getCustomerById(id);

        existingCustomer.setFullName(updatedData.getFullName());
        existingCustomer.setPhoneNumber(updatedData.getPhoneNumber());
        existingCustomer.setEmail(updatedData.getEmail());
        existingCustomer.setAddress(updatedData.getAddress());
        existingCustomer.setCity(updatedData.getCity());
        existingCustomer.setDateOfBirth(updatedData.getDateOfBirth());
        if (updatedData.getAvatarUrl() != null && !updatedData.getAvatarUrl().isBlank()) {
            existingCustomer.setAvatarUrl(updatedData.getAvatarUrl());
        }
        if (updatedData.getLoyaltyPoints() != null) {
            existingCustomer.setLoyaltyPoints(updatedData.getLoyaltyPoints());
        }
        if (updatedData.getRedeemablePoints() != null) {
            existingCustomer.setRedeemablePoints(updatedData.getRedeemablePoints());
        }
        existingCustomer.setUpdatedAt(LocalDateTime.now());

        return customerRepository.save(existingCustomer);
    }

    public List<Customer> searchCustomers(String query) {
        if (query == null || query.isBlank()) {
            return getAllCustomers();
        }
        List<Customer> results = customerRepository
                .findByFullNameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrPhoneNumberContainingIgnoreCase(query, query, query);
        if (results.isEmpty() && query.matches("\\d+")) {
            Long id = Long.parseLong(query);
            customerRepository.findById(id).ifPresent(results::add);
        }
        return results;
    }

    public Integer getLoyaltyPoints(Long id) {
        Customer customer = getCustomerById(id);
        return customer.getLoyaltyPoints() != null ? customer.getLoyaltyPoints() : 0;
    }

    public Integer getRedeemablePoints(Long id) {
        Customer customer = getCustomerById(id);
        return customer.getRedeemablePoints() != null ? customer.getRedeemablePoints() : 0;
    }

    public void deleteCustomer(Long id) {
        Customer customer = getCustomerById(id);
        if (customer.getUserId() != null) {
            userRepository.deleteById(customer.getUserId());
        }
        customerRepository.deleteById(id);
    }
}