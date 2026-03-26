// ID: ATE/7749/15
package com.shopwave;

import com.shopwave.model.Category;
import com.shopwave.model.Product;
import com.shopwave.repository.CategoryRepository;
import com.shopwave.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

    @Override
    public void run(String... args) throws Exception {
        Category electronics = categoryRepository.save(
                Category.builder()
                        .name("Electronics")
                        .description("Phones, laptops, and gadgets")
                        .build()
        );

        Category clothing = categoryRepository.save(
                Category.builder()
                        .name("Clothing")
                        .description("Shirts, shoes, and accessories")
                        .build()
        );

        // Create some products
        productRepository.save(Product.builder()
                .name("Laptop Pro 15")
                .description("High performance laptop for developers")
                .price(new BigDecimal("1299.99"))
                .stock(25)
                .category(electronics)
                .build()
        );

        productRepository.save(Product.builder()
                .name("Wireless Headphones")
                .description("Noise-cancelling over-ear headphones")
                .price(new BigDecimal("249.99"))
                .stock(100)
                .category(electronics)
                .build()
        );

        productRepository.save(Product.builder()
                .name("Running Shoes")
                .description("Lightweight shoes for long distance running")
                .price(new BigDecimal("89.99"))
                .stock(60)
                .category(clothing)
                .build()
        );

        System.out.println("Data seeded successfully.");
    }
}
