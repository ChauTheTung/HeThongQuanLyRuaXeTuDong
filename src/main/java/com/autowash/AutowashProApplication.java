package com.autowash;

import com.autowash.entity.LoyaltyTier;
import com.autowash.entity.User;
import com.autowash.repository.LoyaltyTierRepository;
import com.autowash.repository.UserRepository;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.List;

@SpringBootApplication
@EnableScheduling
public class AutowashProApplication {

    public static void main(String[] args) {
        SpringApplication.run(AutowashProApplication.class, args);
    }

    @Bean
    public ApplicationRunner dataInitializer(UserRepository userRepository,
                                             LoyaltyTierRepository tierRepository,
                                             BCryptPasswordEncoder passwordEncoder) {
        return args -> {
            if (userRepository.findByUsername("admin").isEmpty()) {
                User admin = new User("admin", passwordEncoder.encode("admin1234"), "ROLE_ADMIN");
                userRepository.save(admin);
            }

            if (tierRepository.count() == 0) {
                LoyaltyTier bronze = new LoyaltyTier();
                bronze.setName("Bronze");
                bronze.setMinPoints(0);
                bronze.setMaxPoints(499);
                bronze.setDiscountPercent(0.0);

                LoyaltyTier silver = new LoyaltyTier();
                silver.setName("Silver");
                silver.setMinPoints(500);
                silver.setMaxPoints(1199);
                silver.setDiscountPercent(5.0);

                LoyaltyTier gold = new LoyaltyTier();
                gold.setName("Gold");
                gold.setMinPoints(1200);
                gold.setMaxPoints(999999);
                gold.setDiscountPercent(10.0);

                tierRepository.saveAll(List.of(bronze, silver, gold));
            }
        };
    }
}