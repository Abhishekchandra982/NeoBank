package com.example.neobank.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    public void sendOtp(String email, String otp) {

        try {

            SimpleMailMessage message = new SimpleMailMessage();

            message.setFrom(fromEmail);
            message.setTo(email);
            message.setSubject("NeoBank OTP");
            message.setText("Your OTP is: " + otp);

            mailSender.send(message);

            System.out.println("EMAIL SENT SUCCESSFULLY");

        } catch (Exception e) {

            e.printStackTrace();
            throw new RuntimeException("Mail sending failed", e);
        }
    }
}