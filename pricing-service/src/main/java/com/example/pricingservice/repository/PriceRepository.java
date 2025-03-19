package com.example.pricingservice.repository;

import com.example.pricingservice.model.Price;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PriceRepository extends JpaRepository<Price,Long> {
    Optional<Price> findByProductId(Long productId);
}
