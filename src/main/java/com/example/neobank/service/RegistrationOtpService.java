package com.example.neobank.service;

import com.example.neobank.entity.RegistrationOtp;
import com.example.neobank.repository.RegistrationOtpRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Random;

@Service
public class RegistrationOtpService {

    @Autowired
    private RegistrationOtpRepository otpRepository;

    @Autowired
    private EmailService emailService;

    public void generateOtp(String email){
        String otpValue = String.valueOf(new Random().nextInt(900000) + 100000);
        otpRepository.deleteByEmail(email);

        RegistrationOtp otp = new RegistrationOtp();
        otp.setEmail(email);
        otp.setOtp(otpValue);
        otp.setAttempts(0);
        otp.setExpiryTime(LocalDateTime.now().plusMinutes(5));

        otpRepository.save(otp);

        emailService.sendOtp(email, otpValue);

    }
    public void verifyOtp(String email, String enteredOtp){

        RegistrationOtp otp = otpRepository
                .findTopByEmailAndUsedFalseOrderByExpiryTimeDesc(email)
                .orElseThrow(() -> new RuntimeException("OTP not found"));

        if(otp.getAttempts() >= 3){
            throw new RuntimeException(("OTP expired"));
        }

        if(!otp.getOtp().equals(enteredOtp)){
            otp.setAttempts(otp.getAttempts() + 1);
            otpRepository.save(otp);
            throw new RuntimeException("Invalid OTP");
        }

        otp.setUsed(true);
        otpRepository.save(otp);
    }

}
