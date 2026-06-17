package com.autowash.controller;

import com.autowash.entity.WashHistory;
import com.autowash.service.WashHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/wash-history")
@CrossOrigin(origins = "*")
public class WashHistoryController {

    @Autowired
    private WashHistoryService washHistoryService;

    @GetMapping("/customer/{customerId}")
    public List<WashHistory> getByCustomer(@PathVariable Long customerId) {
        return washHistoryService.getWashHistoryByCustomer(customerId);
    }

    @GetMapping("/vehicle/{vehicleId}")
    public List<WashHistory> getByVehicle(@PathVariable Long vehicleId) {
        return washHistoryService.getWashHistoryByVehicle(vehicleId);
    }

    @PostMapping
    public ResponseEntity<WashHistory> recordWashHistory(@RequestBody WashHistory history) {
        return ResponseEntity.ok(washHistoryService.recordWashHistory(history));
    }
}
