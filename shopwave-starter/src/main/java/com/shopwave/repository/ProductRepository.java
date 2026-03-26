// Name: Peter Kinfe
// ID: ATE/7749/15
package com.shopwave.repository;

import com.shopwave.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByCategoryId(Long categoryId);

    List<Product> findByPriceLessThanEqual(BigDecimal maxPrice);

    List<Product> findByNameContainingIgnoreCase(String keyword);

    Optional<Product> findTopByOrderByPriceDesc();
}