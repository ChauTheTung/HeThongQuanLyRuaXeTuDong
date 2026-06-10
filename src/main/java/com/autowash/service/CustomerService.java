package com.autowash.service;

import com.autowash.entity.Customer;
import com.autowash.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Service
public class CustomerService {

    @Autowired
    private CustomerRepository customerRepository;

    public Customer getCustomerById(Long id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy khách hàng với ID: " + id));
    }

    public Customer getCustomerByUserId(Long userId) {
        return customerRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy khách hàng liên kết với User ID: " + userId));
    }

    public Customer updateProfile(Long id, Customer updatedData) {
        Customer existingCustomer = getCustomerById(id);
        
        existingCustomer.setFullName(updatedData.getFullName());
        existingCustomer.setPhoneNumber(updatedData.getPhoneNumber());
        existingCustomer.setEmail(updatedData.getEmail());
        existingCustomer.setUpdatedAt(LocalDateTime.now());
        
        return customerRepository.save(existingCustomer);
    }

    public Integer getLoyaltyPoints(Long id) {
        Customer customer = getCustomerById(id);
        return customer.getLoyaltyPoints() != null ? customer.getLoyaltyPoints() : 0;
    }
}