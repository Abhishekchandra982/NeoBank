package com.example.neobank.controller;

import com.example.neobank.dto.*;
import com.example.neobank.entity.BankAccount;
import com.example.neobank.entity.PasswordResetOtp;
import com.example.neobank.entity.Role;
import com.example.neobank.entity.User;
import com.example.neobank.repository.BankAccountRepository;
import com.example.neobank.repository.PasswordResetOtpRepository;
import com.example.neobank.repository.UserRepository;
import com.example.neobank.security.JwtUtil;
import com.example.neobank.service.EmailService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserRepository userRepository;
    private final BankAccountRepository bankAccountRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final PasswordResetOtpRepository otpRepository;

    @Autowired
    private EmailService emailService;

    private String generateOtp(){
        Random random = new Random();
        int otp = 100000 + random.nextInt(900000);
        return String.valueOf(otp);
    }


    @Transactional
    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody ForgotPasswordRequest request) {

        Optional<User> user = userRepository.findByEmail(request.getEmail());

        if(user.isEmpty()){
            return ResponseEntity.badRequest().body("User not found");
        }

        otpRepository.deleteByEmail(request.getEmail());

        String otp = String.valueOf(new Random().nextInt(900000) + 100000);

        PasswordResetOtp otpEntity = new PasswordResetOtp();
        otpEntity.setEmail(request.getEmail());
        otpEntity.setOtp(otp);
        otpEntity.setAttempts(0);
        otpEntity.setExpiryTime(LocalDateTime.now().plusMinutes(5));

        otpRepository.save(otpEntity);

        emailService.sendOtp(request.getEmail(), otp);

        return ResponseEntity.ok("OTP sent successfully");
    }




    // REGISTER
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(Map.of("message", "Email already exists"));
        }

        // 🔹 Create User
        User user = User.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER)
                .build();

        User savedUser = userRepository.save(user);

        // 🔹 Auto-create Bank Account
        BankAccount account = BankAccount.builder()
                .accountNumber(generateAccountNumber())
                .balance(BigDecimal.ZERO)
                .user(savedUser)
                .build();

        bankAccountRepository.save(account);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(Map.of(
                        "message", "User registered and bank account created",
                        "accountNumber", account.getAccountNumber()
                ));
    }

    // LOGIN
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Invalid credentials"));
        }

        String token = jwtUtil.generateToken(
                user.getEmail(),
                user.getRole().name()
        );

        return ResponseEntity.ok(
                Map.of("token", token)
        );
    }




    private String generateAccountNumber() {
        return "AC" + System.currentTimeMillis();
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<?> verifyOtp(@RequestBody VerifyOtpRequest request) {

        Optional<PasswordResetOtp> otpData =
                otpRepository.findByEmail(request.getEmail());

        if (otpData.isEmpty()) {
            return ResponseEntity.badRequest().body("OTP not found");
        }

        PasswordResetOtp storedOtp = otpData.get();


        if (storedOtp.getAttempts() >= 3) {
            otpRepository.delete(storedOtp);
            return ResponseEntity.badRequest().body("OTP blocked after 3 attempts");
        }


        if (storedOtp.getExpiryTime().isBefore(LocalDateTime.now())) {
            otpRepository.delete(storedOtp);
            return ResponseEntity.badRequest().body("OTP expired");
        }


        if (!storedOtp.getOtp().equals(request.getOtp())) {

            storedOtp.setAttempts(storedOtp.getAttempts() + 1);
            otpRepository.save(storedOtp);

            return ResponseEntity.badRequest().body("Invalid OTP");
        }

        return ResponseEntity.ok("OTP verified successfully");
    }

    @Transactional
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordRequest request){

        Optional<User> userOptional = userRepository.findByEmail(request.getEmail());

        if(userOptional.isEmpty()){
            return ResponseEntity.badRequest().body("User not found");
        }

        User user = userOptional.get();

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        otpRepository.deleteByEmail(request.getEmail());

        return ResponseEntity.ok("Password reset successful");
    }
}
