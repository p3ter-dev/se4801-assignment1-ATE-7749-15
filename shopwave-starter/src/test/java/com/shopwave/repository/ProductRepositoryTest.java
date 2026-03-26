// Name: Peter Kinfe
// ID: ATE/7749/15
package com.shopwave.repository;

import com.shopwave.model.Category;
import com.shopwave.model.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class ProductRepositoryTest {
    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ProductRepository productRepository;

    private Category electronics;

    @BeforeEach
    void setUp() {
        electronics = entityManager.persistAndFlush(
                Category.builder()
                        .name("Electronics")
                        .description("Tech products")
                        .build()
        );

        entityManager.persistAndFlush(Product.builder()
                .name("Laptop Pro 15")
                .description("Developer laptop")
                .price(new BigDecimal("1299.99"))
                .stock(25)
                .category(electronics)
                .build());

        entityManager.persistAndFlush(Product.builder()
                .name("Laptop Air 13")
                .description("Lightweight laptop")
                .price(new BigDecimal("899.99"))
                .stock(30)
                .category(electronics)
                .build());

        entityManager.persistAndFlush(Product.builder()
                .name("Wireless Headphones")
                .description("Noise cancelling")
                .price(new BigDecimal("249.99"))
                .stock(50)
                .category(electronics)
                .build());
    }

    @Test
    void findByNameContainingIgnoreCase_withMatchingKeyword_shouldReturnMatchingProducts() {
        List<Product> results = productRepository
                .findByNameContainingIgnoreCase("laptop");
        assertThat(results).hasSize(2);
        assertThat(results)
                .extracting(Product::getName)
                .containsExactlyInAnyOrder("Laptop Pro 15", "Laptop Air 13");
    }

    @Test
    void findByNameContainingIgnoreCase_withUppercaseKeyword_shouldReturnMatchingProducts() {
        List<Product> results = productRepository
                .findByNameContainingIgnoreCase("LAPTOP");

        assertThat(results).hasSize(2);
    }

    @Test
    void findByNameContainingIgnoreCase_withNoMatch_shouldReturnEmptyList() {
        List<Product> results = productRepository
                .findByNameContainingIgnoreCase("tablet");
        assertThat(results).isEmpty();
    }

    @Test
    void findByPriceLessThanEqual_shouldReturnOnlyProductsWithinPriceRange() {
        List<Product> results = productRepository
                .findByPriceLessThanEqual(new BigDecimal("300.00"));
        assertThat(results).hasSize(1);
        assertThat(results.get(0).getName()).isEqualTo("Wireless Headphones");
    }

    @Test
    void findTopByOrderByPriceDesc_shouldReturnMostExpensiveProduct() {
        var result = productRepository.findTopByOrderByPriceDesc();
        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("Laptop Pro 15");
        assertThat(result.get().getPrice())
                .isEqualByComparingTo(new BigDecimal("1299.99"));
    }

    @Test
    void findByCategoryId_shouldReturnAllProductsInCategory() {
        List<Product> results = productRepository
                .findByCategoryId(electronics.getId());
        assertThat(results).hasSize(3);
    }
}