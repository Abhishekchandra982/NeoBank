package com.example.neobank.repository;

import com.example.neobank.entity.RegistrationOtp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface RegistrationOtpRepository extends JpaRepository<RegistrationOtp, Long> {

    Optional<RegistrationOtp> findTopByEmailAndUsedFalseOrderByExpiryTimeDesc(String email);

    @Transactional
    void deleteByEmail(String email);
}