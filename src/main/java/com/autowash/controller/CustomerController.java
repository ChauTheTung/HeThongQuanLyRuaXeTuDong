package com.autowash.controller;

import com.autowash.dto.LoyaltyDTO;
import com.autowash.entity.Customer;
import com.autowash.service.CustomerService;
import com.autowash.service.LoyaltyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/customers")
@CrossOrigin(origins = "*")
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    @Autowired
    private LoyaltyService loyaltyService;

    @GetMapping("/{id}")
    public ResponseEntity<Customer> getCustomerProfile(@PathVariable Long id) {
        return ResponseEntity.ok(customerService.getCustomerById(id));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<Customer> getCustomerProfileByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(customerService.getCustomerByUserId(userId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Customer> updateCustomerProfile(@PathVariable Long id, @RequestBody Customer customer) {
        return ResponseEntity.ok(customerService.updateProfile(id, customer));
    }

    @GetMapping("/{id}/points")
    public ResponseEntity<Integer> getCustomerPoints(@PathVariable Long id) {
        return ResponseEntity.ok(customerService.getLoyaltyPoints(id));
    }

    @GetMapping("/{id}/loyalty")
    public ResponseEntity<LoyaltyDTO> getCustomerLoyalty(@PathVariable Long id) {
        return ResponseEntity.ok(loyaltyService.getLoyaltyInfo(id));
    }
}

