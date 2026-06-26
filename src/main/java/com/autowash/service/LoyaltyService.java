package com.autowash.service;

import com.autowash.dto.LoyaltyDTO;
import com.autowash.entity.Customer;
import com.autowash.entity.LoyaltyTier;
import com.autowash.entity.PointTransaction;
import com.autowash.exception.ResourceNotFoundException;
import com.autowash.repository.CustomerRepository;
import com.autowash.repository.LoyaltyTierRepository;
import com.autowash.repository.PointTransactionRepository;
import com.autowash.repository.BookingRepository;
import org.springframework.scheduling.annotation.Scheduled;
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
    private final BookingRepository bookingRepository;

    public LoyaltyService(CustomerRepository customerRepository,
                          LoyaltyTierRepository loyaltyTierRepository,
                          PointTransactionRepository pointTransactionRepository,
                          BookingRepository bookingRepository) {
        this.customerRepository = customerRepository;
        this.loyaltyTierRepository = loyaltyTierRepository;
        this.pointTransactionRepository = pointTransactionRepository;
        this.bookingRepository = bookingRepository;
    }

    public LoyaltyDTO getLoyaltyInfo(Long customerId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + customerId));

        int points = customer.getLoyaltyPoints() != null ? customer.getLoyaltyPoints() : 0; // accumulated points
        int redeemable = customer.getRedeemablePoints() != null ? customer.getRedeemablePoints() : 0;
        LoyaltyTier tier = determineTier(points);
        LoyaltyDTO dto = new LoyaltyDTO();
        dto.setCustomerId(customer.getId());
        dto.setLoyaltyPoints(points);
        dto.setRedeemablePoints(redeemable);
        dto.setCurrentTier(tier != null ? tier.getName() : "Bronze");

        loyaltyTierRepository.findFirstByMinPointsGreaterThanOrderByMinPointsAsc(points)
                .ifPresent(next -> dto.setNextTierPoints(next.getMinPoints()));

        return dto;
    }

    public LoyaltyDTO addPoints(Long customerId, int points, String description) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + customerId));

        // Earned points increase both accumulated (for tier) and redeemable balances
        int updatedAccum = customer.getLoyaltyPoints() + points;
        int updatedRedeemable = (customer.getRedeemablePoints() != null ? customer.getRedeemablePoints() : 0) + points;
        customer.setLoyaltyPoints(updatedAccum);
        customer.setRedeemablePoints(updatedRedeemable);
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
        // Check redeemable balance (points that can be spent)
        int redeemable = customer.getRedeemablePoints() != null ? customer.getRedeemablePoints() : 0;

        // Prevent frequent re-redemption: disallow if last redeem was within 30 days
        PointTransaction lastRedeem = pointTransactionRepository.findFirstByCustomerIdAndTransactionTypeOrderByCreatedAtDesc(customerId, "REDEEM");
        if (lastRedeem != null && lastRedeem.getCreatedAt().isAfter(LocalDateTime.now().minusDays(30))) {
            throw new IllegalArgumentException("Bạn đã đổi điểm trong 30 ngày gần đây. Vui lòng thử lại sau.");
        }

        if (redeemable < points) {
            throw new IllegalArgumentException("Không đủ điểm để quy đổi.");
        }

        int updatedRedeemable = redeemable - points;
        customer.setRedeemablePoints(updatedRedeemable);
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

    // Daily job: reset redeemable points to 0 for customers who haven't used any booking in the last 30 days
    @Scheduled(cron = "0 0 3 * * ?") // every day at 03:00
    public void resetRedeemablePointsForInactiveCustomers() {
        LocalDateTime cutoff = LocalDateTime.now().minusDays(30);
        List<Customer> customers = customerRepository.findAll();

        for (Customer customer : customers) {
            boolean hadRecentBooking = !bookingRepository.findByCustomerIdAndBookingTimeAfter(customer.getId(), cutoff).isEmpty();
            if (!hadRecentBooking && customer.getRedeemablePoints() != null && customer.getRedeemablePoints() > 0) {
                int old = customer.getRedeemablePoints();
                customer.setRedeemablePoints(0);
                customer.setUpdatedAt(LocalDateTime.now());
                customerRepository.save(customer);

                PointTransaction transaction = new PointTransaction();
                transaction.setCustomerId(customer.getId());
                transaction.setPoints(-old);
                transaction.setTransactionType("EXPIRE");
                transaction.setDescription("Reset điểm quy đổi do không sử dụng dịch vụ 30 ngày");
                transaction.setCreatedAt(LocalDateTime.now());
                pointTransactionRepository.save(transaction);
            }
        }
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
