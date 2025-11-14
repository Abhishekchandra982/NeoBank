package com.example.neobank.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "bank_user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String uid;
    private String firstName;
    private String lastname;

    @Column(nullable = false, unique = true)
    private String username;

    private LocalDate dob;
    private Long tel;
    private String tag;
    private String password;
    private String gender;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;



    private List<String> roles;

    @OneToOne(mappedBy = "owner")
    private Card card;

    @OneToMany(mappedBy = "owner",cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Transaction> transactions;

    @OneToMany(mappedBy = "owner",cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Account> accounts;

}
