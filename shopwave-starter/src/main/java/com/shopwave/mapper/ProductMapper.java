// Name: Peter Kinfe
// ID: ATE/7749/15
package com.shopwave.mapper;

import com.shopwave.dto.ProductDTO;
import com.shopwave.model.Product;
import org.springframework.stereotype.Component;

@Component
public class ProductMapper {
    public ProductDTO toDTO(Product product) {
        return ProductDTO.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .stock(product.getStock())
                .categoryName(product.getCategory() != null
                        ? product.getCategory().getName()
                        : null)
                .createdAt(product.getCreatedAt())
                .build();
    }
}