package com.autowash.controller;

import com.autowash.dto.LoyaltyDTO;
import com.autowash.service.LoyaltyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/loyalty")
@CrossOrigin(origins = "*")
public class LoyaltyController {

    @Autowired
    private LoyaltyService loyaltyService;

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<LoyaltyDTO> getLoyaltyInfo(@PathVariable Long customerId) {
        return ResponseEntity.ok(loyaltyService.getLoyaltyInfo(customerId));
    }

    @PostMapping("/customer/{customerId}/earn")
    public ResponseEntity<LoyaltyDTO> earnPoints(
            @PathVariable Long customerId,
            @RequestParam int points,
            @RequestParam(required = false) String description
    ) {
        return ResponseEntity.ok(loyaltyService.addPoints(customerId, points, description));
    }

    @PostMapping("/customer/{customerId}/redeem")
    public ResponseEntity<LoyaltyDTO> redeemPoints(
            @PathVariable Long customerId,
            @RequestParam int points,
            @RequestParam(required = false) String description
    ) {
        return ResponseEntity.ok(loyaltyService.redeemPoints(customerId, points, description));
    }
}
