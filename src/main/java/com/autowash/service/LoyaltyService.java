package com.autowash.service;

import com.autowash.dto.LoyaltyDTO;
import com.autowash.entity.Customer;
import com.autowash.entity.LoyaltyTier;
import com.autowash.entity.PointTransaction;
import com.autowash.exception.ResourceNotFoundException;
import com.autowash.repository.CustomerRepository;
import com.autowash.repository.LoyaltyTierRepository;
import com.autowash.repository.PointTransactionRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
public class LoyaltyService {

    private final CustomerRepository customerRepository;
    private final LoyaltyTierRepository loyaltyTierRepository;
    private final PointTransactionRepository pointTransactionRepository;

    public LoyaltyService(CustomerRepository customerRepository,
                          LoyaltyTierRepository loyaltyTierRepository,
                          PointTransactionRepository pointTransactionRepository) {
        this.customerRepository = customerRepository;
        this.loyaltyTierRepository = loyaltyTierRepository;
        this.pointTransactionRepository = pointTransactionRepository;
    }

    public LoyaltyDTO getLoyaltyInfo(Long customerId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + customerId));

        int points = customer.getLoyaltyPoints() != null ? customer.getLoyaltyPoints() : 0;
        LoyaltyTier tier = determineTier(points);
        LoyaltyDTO dto = new LoyaltyDTO();
        dto.setCustomerId(customer.getId());
        dto.setLoyaltyPoints(points);
        dto.setCurrentTier(tier != null ? tier.getName() : "Bronze");

        loyaltyTierRepository.findFirstByMinPointsGreaterThanOrderByMinPointsAsc(points)
                .ifPresent(next -> dto.setNextTierPoints(next.getMinPoints()));

        return dto;
    }

    public LoyaltyDTO addPoints(Long customerId, int points, String description) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + customerId));

        int updatedPoints = customer.getLoyaltyPoints() + points;
        customer.setLoyaltyPoints(updatedPoints);
        customer.setUpdatedAt(LocalDateTime.now());
        customerRepository.save(customer);

        PointTransaction transaction = new PointTransaction();
        transaction.setCustomerId(customerId);
        transaction.setPoints(points);
        transaction.setTransactionType("EARN");
        transaction.setDescription(description != null ? description : "Điểm thưởng" );
        transaction.setCreatedAt(LocalDateTime.now());
        pointTransactionRepository.save(transaction);

        return getLoyaltyInfo(customerId);
    }

    public LoyaltyDTO redeemPoints(Long customerId, int points, String description) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + customerId));

        if (points <= 0) {
            throw new IllegalArgumentException("Số điểm quy đổi phải lớn hơn 0.");
        }
        if (customer.getLoyaltyPoints() < points) {
            throw new IllegalArgumentException("Không đủ điểm để quy đổi.");
        }

        int updatedPoints = customer.getLoyaltyPoints() - points;
        customer.setLoyaltyPoints(updatedPoints);
        customer.setUpdatedAt(LocalDateTime.now());
        customerRepository.save(customer);

        PointTransaction transaction = new PointTransaction();
        transaction.setCustomerId(customerId);
        transaction.setPoints(-points);
        transaction.setTransactionType("REDEEM");
        transaction.setDescription(description != null ? description : "Quy đổi điểm");
        transaction.setCreatedAt(LocalDateTime.now());
        pointTransactionRepository.save(transaction);

        return getLoyaltyInfo(customerId);
    }

    public LoyaltyTier createTier(LoyaltyTier tier) {
        tier.setName(tier.getName().trim());
        tier.setCreatedAt(LocalDateTime.now());
        tier.setUpdatedAt(LocalDateTime.now());
        return loyaltyTierRepository.save(tier);
    }

    public void deleteTier(Long id) {
        loyaltyTierRepository.deleteById(id);
    }

    public List<LoyaltyTier> getAllTiers() {
        return loyaltyTierRepository.findAll().stream()
                .sorted(Comparator.comparing(LoyaltyTier::getMinPoints))
                .toList();
    }

    public void expireOldPoints() {
        LocalDateTime cutoff = LocalDateTime.now().minusMonths(12);
        List<Customer> customers = customerRepository.findAll();

        for (Customer customer : customers) {
            int expiredPoints = pointTransactionRepository
                    .findByCustomerIdAndTransactionTypeAndCreatedAtBefore(customer.getId(), "EARN", cutoff)
                    .stream()
                    .mapToInt(PointTransaction::getPoints)
                    .sum();

            if (expiredPoints > 0) {
                customer.setLoyaltyPoints(Math.max(0, customer.getLoyaltyPoints() - expiredPoints));
                customer.setUpdatedAt(LocalDateTime.now());
                customerRepository.save(customer);
            }
        }
    }

    private LoyaltyTier determineTier(Integer points) {
        if (points == null) {
            return null;
        }
        List<LoyaltyTier> tiers = loyaltyTierRepository.findAll();
        return tiers.stream()
                .filter(t -> points >= t.getMinPoints() && (t.getMaxPoints() == null || points <= t.getMaxPoints()))
                .max(Comparator.comparing(LoyaltyTier::getMinPoints))
                .orElse(null);
    }
}
