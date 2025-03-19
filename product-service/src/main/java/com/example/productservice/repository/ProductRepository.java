package com.example.productservice.repository;

import com.example.productservice.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
//    List<Product> findByCategory_IdAndInventoryGreaterThan(Long categoryId, int inventory);
    Optional<Product> findByName(String name);


    @Query("SELECT p FROM Product p WHERE p.category.name = :categoryName ORDER BY " +
            "CASE WHEN :sortBy = 'name' THEN p.name " +
            "WHEN :sortBy = 'price' THEN p.price " +
            "ELSE p.name END ASC")
    List<Product> findAvailableProductsByCategoryName(String categoryName, String sortBy);
}
