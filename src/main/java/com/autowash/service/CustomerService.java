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
        return customerRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy khách hàng liên kết với User ID: " + userId));
    }

    public Customer getCustomerByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng: " + username));
        return getCustomerByUserId(user.getId());
    }

    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }

    public Customer updateProfile(Long id, Customer updatedData) {
        Customer existingCustomer = getCustomerById(id);

        existingCustomer.setFullName(updatedData.getFullName());
        existingCustomer.setPhoneNumber(updatedData.getPhoneNumber());
        existingCustomer.setEmail(updatedData.getEmail());
        if (updatedData.getLoyaltyPoints() != null) {
            existingCustomer.setLoyaltyPoints(updatedData.getLoyaltyPoints());
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

    public void deleteCustomer(Long id) {
        Customer customer = getCustomerById(id);
        if (customer.getUserId() != null) {
            userRepository.deleteById(customer.getUserId());
        }
        customerRepository.deleteById(id);
    }
}