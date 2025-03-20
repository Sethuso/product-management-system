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
            "CASE WHEN :sortBy = 'low' THEN p.price END ASC, " +
            "CASE WHEN :sortBy = 'high' THEN p.price END DESC, " +
            "p.name ASC")
    List<Product> findAvailableProductsByCategoryName(@Param("categoryName") String categoryName, @Param("sortBy") String sortBy);

}
