// Name: Peter Kinfe
// ID: ATE/7749/15
package com.shopwave.service;

import com.shopwave.dto.CreateProductRequest;
import com.shopwave.dto.ProductDTO;
import com.shopwave.exception.ProductNotFoundException;
import com.shopwave.mapper.ProductMapper;
import com.shopwave.model.Category;
import com.shopwave.model.Product;
import com.shopwave.repository.CategoryRepository;
import com.shopwave.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {
    @Mock
    private ProductRepository productRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private ProductMapper productMapper;

    @InjectMocks
    private ProductService productService;

    private Category testCategory;
    private Product testProduct;
    private ProductDTO testProductDTO;
    private CreateProductRequest createRequest;

    @BeforeEach
    void setUp() {
        testCategory = Category.builder()
                .id(1L)
                .name("Electronics")
                .description("Phones and gadgets")
                .build();

        testProduct = Product.builder()
                .id(1L)
                .name("Laptop Pro 15")
                .description("Developer laptop")
                .price(new BigDecimal("1299.99"))
                .stock(25)
                .category(testCategory)
                .build();

        testProductDTO = ProductDTO.builder()
                .id(1L)
                .name("Laptop Pro 15")
                .description("Developer laptop")
                .price(new BigDecimal("1299.99"))
                .stock(25)
                .categoryName("Electronics")
                .build();

        createRequest = CreateProductRequest.builder()
                .name("Laptop Pro 15")
                .description("Developer laptop")
                .price(new BigDecimal("1299.99"))
                .stock(25)
                .categoryId(1L)
                .build();
    }

    @Test
    void createProduct_whenCategoryExists_shouldReturnProductDTO() {
        when(categoryRepository.findById(1L))
                .thenReturn(Optional.of(testCategory));
        when(productRepository.save(any(Product.class)))
                .thenReturn(testProduct);
        when(productMapper.toDTO(testProduct))
                .thenReturn(testProductDTO);
        ProductDTO result = productService.createProduct(createRequest);
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Laptop Pro 15");
        assertThat(result.getPrice()).isEqualByComparingTo(new BigDecimal("1299.99"));
        assertThat(result.getCategoryName()).isEqualTo("Electronics");
        verify(productRepository).save(any(Product.class));
    }

    @Test
    void createProduct_whenCategoryNotFound_shouldThrowException() {
        when(categoryRepository.findById(1L))
                .thenReturn(Optional.empty());
        assertThatThrownBy(() -> productService.createProduct(createRequest))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Category not found with id: 1");
    }

    @Test
    void getProductById_whenProductExists_shouldReturnProductDTO() {
        when(productRepository.findById(1L))
                .thenReturn(Optional.of(testProduct));
        when(productMapper.toDTO(testProduct))
                .thenReturn(testProductDTO);
        ProductDTO result = productService.getProductById(1L);
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Laptop Pro 15");
    }

    @Test
    void getProductById_whenProductNotFound_shouldThrowProductNotFoundException() {
        when(productRepository.findById(999L))
                .thenReturn(Optional.empty());
        assertThatThrownBy(() -> productService.getProductById(999L))
                .isInstanceOf(ProductNotFoundException.class)
                .hasMessageContaining("999");
    }

    @Test
    void updateStock_whenDeltaWouldMakeStockNegative_shouldThrowIllegalArgumentException() {
        when(productRepository.findById(1L))
                .thenReturn(Optional.of(testProduct));
        assertThatThrownBy(() -> productService.updateStock(1L, -100))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Insufficient stock");
    }
}