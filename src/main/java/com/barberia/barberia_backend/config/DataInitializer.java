package com.barberia.barberia_backend.config;

import com.barberia.barberia_backend.businesshour.BusinessHour;
import com.barberia.barberia_backend.businesshour.BusinessHourRepository;
import com.barberia.barberia_backend.common.enums.Role;
import com.barberia.barberia_backend.product.Product;
import com.barberia.barberia_backend.product.ProductRepository;
import com.barberia.barberia_backend.user.User;
import com.barberia.barberia_backend.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalTime;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

        private final BusinessHourRepository businessHourRepository;
        private final UserRepository userRepository;
        private final PasswordEncoder passwordEncoder;
        private final ProductRepository productRepository;

        @Override
        public void run(String... args) {
                createInitialBusinessHours();
                createAdminIfNotExists();
                createProductsIfNotExists();
        }

        private void createInitialBusinessHours() {
                createBusinessHourIfNotExists(DayOfWeek.MONDAY, LocalTime.of(9, 0), LocalTime.of(18, 0), true);
                createBusinessHourIfNotExists(DayOfWeek.TUESDAY, LocalTime.of(9, 0), LocalTime.of(18, 0), true);
                createBusinessHourIfNotExists(DayOfWeek.WEDNESDAY, LocalTime.of(9, 0), LocalTime.of(18, 0), true);
                createBusinessHourIfNotExists(DayOfWeek.THURSDAY, LocalTime.of(9, 0), LocalTime.of(18, 0), true);
                createBusinessHourIfNotExists(DayOfWeek.FRIDAY, LocalTime.of(9, 0), LocalTime.of(18, 0), true);
                createBusinessHourIfNotExists(DayOfWeek.SATURDAY, LocalTime.of(9, 0), LocalTime.of(14, 0), true);
                createBusinessHourIfNotExists(DayOfWeek.SUNDAY, null, null, false);
        }

        private void createBusinessHourIfNotExists(DayOfWeek dayOfWeek, LocalTime openTime, LocalTime closeTime,
                        Boolean active) {
                if (businessHourRepository.existsByDayOfWeek(dayOfWeek)) {
                        return;
                }

                BusinessHour businessHour = BusinessHour.builder()
                                .dayOfWeek(dayOfWeek)
                                .openTime(openTime)
                                .closeTime(closeTime)
                                .active(active)
                                .build();

                businessHourRepository.save(businessHour);
        }

        private void createAdminIfNotExists() {
                String adminEmail = "admin@barberia.com";

                userRepository.findByEmail(adminEmail).ifPresentOrElse(existingAdmin -> {
                        boolean updated = false;

                        if (existingAdmin.getRole() != Role.ADMIN) {
                                existingAdmin.setRole(Role.ADMIN);
                                updated = true;
                        }

                        if (updated) {
                                userRepository.save(existingAdmin);
                        }
                }, () -> {
                        User admin = User.builder()
                                        .name("Administrador")
                                        .email(adminEmail)
                                        .password(passwordEncoder.encode("admin123"))
                                        .role(Role.ADMIN)
                                        .build();

                        userRepository.save(admin);
                });
        }

        private void createProductsIfNotExists() {
                if (productRepository.count() > 0) {
                        return;
                }

                Product pomade = new Product();
                pomade.setName("Pomada para cabello");
                pomade.setDescription("Pomada de fijación fuerte para peinados clásicos y modernos.");
                pomade.setPrice(new BigDecimal("8.50"));
                pomade.setStock(20);
                pomade.setImageUrl("https://example.com/images/pomada.jpg");
                pomade.setActive(true);

                Product beardShampoo = new Product();
                beardShampoo.setName("Shampoo para barba");
                beardShampoo.setDescription("Shampoo especial para limpieza y cuidado de barba.");
                beardShampoo.setPrice(new BigDecimal("10.00"));
                beardShampoo.setStock(15);
                beardShampoo.setImageUrl("https://example.com/images/shampoo-barba.jpg");
                beardShampoo.setActive(true);

                Product beardOil = new Product();
                beardOil.setName("Aceite para barba");
                beardOil.setDescription("Aceite hidratante para barba con aroma suave.");
                beardOil.setPrice(new BigDecimal("12.00"));
                beardOil.setStock(10);
                beardOil.setImageUrl("https://example.com/images/aceite-barba.jpg");
                beardOil.setActive(true);

                Product gel = new Product();
                gel.setName("Gel fijador");
                gel.setDescription("Gel para cabello con fijación media.");
                gel.setPrice(new BigDecimal("6.50"));
                gel.setStock(30);
                gel.setImageUrl("https://example.com/images/gel.jpg");
                gel.setActive(true);

                productRepository.save(pomade);
                productRepository.save(beardShampoo);
                productRepository.save(beardOil);
                productRepository.save(gel);
        }
}