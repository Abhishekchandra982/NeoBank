package com.example.neobank.repository;

import com.example.neobank.entity.Card;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CardRepository extends JpaRepository<Card , String> {
}
