package com.example.neobank.config;

import com.example.neobank.entity.Role;
import com.example.neobank.entity.User;
import com.example.neobank.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AdminInitializer {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @PostConstruct
    public void createAdmin() {

        System.out.println(" AdminInitializer running...");

        String adminEmail = "admin@gmail.com";

        if (userRepository.existsByEmail(adminEmail)) {
            System.out.println(" Admin already exists");
            return;
        }

        User admin = User.builder()
                .fullName("Admin")
                .email(adminEmail)
                .password(passwordEncoder.encode("admin123"))
                .role(Role.ADMIN)
                .isVerified(true)
                .build();

        userRepository.save(admin);

        System.out.println(" Admin created!");
    }
}