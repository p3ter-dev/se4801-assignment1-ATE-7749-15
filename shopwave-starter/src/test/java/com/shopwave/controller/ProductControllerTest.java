// Name: Peter Kinfe
// ID: ATE/7749/15
package com.shopwave.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shopwave.dto.CreateProductRequest;
import com.shopwave.dto.ProductDTO;
import com.shopwave.exception.GlobalExceptionHandler;
import com.shopwave.exception.ProductNotFoundException;
import com.shopwave.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductController.class)
@Import(GlobalExceptionHandler.class)
class ProductControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ProductService productService;

    private ProductDTO sampleProductDTO;

    @BeforeEach
    void setUp() {
        sampleProductDTO = ProductDTO.builder()
                .id(1L)
                .name("Laptop Pro 15")
                .description("Developer laptop")
                .price(new BigDecimal("1299.99"))
                .stock(25)
                .categoryName("Electronics")
                .createdAt(LocalDateTime.now())
                .build();
    }

    // ── Test: GET /api/products returns 200 with paginated body ───────────────

    @Test
    void getAllProducts_shouldReturn200WithPaginatedBody() throws Exception {
        Page<ProductDTO> page = new PageImpl<>(
                List.of(sampleProductDTO),
                PageRequest.of(0, 10),
                1  // total elements
        );
        when(productService.getAllProducts(any())).thenReturn(page);

        mockMvc.perform(get("/api/products")
                        .param("page", "0")
                        .param("size", "10")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                // assert the pagination wrapper fields exist
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].name").value("Laptop Pro 15"))
                .andExpect(jsonPath("$.content[0].price").value(1299.99))
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.totalPages").value(1));
    }

    // ── Test: GET /api/products/999 returns 404 with error JSON ───────────────

    @Test
    void getProductById_whenNotFound_shouldReturn404WithErrorBody() throws Exception {

        // Arrange: program the mock service to throw ProductNotFoundException
        // when asked for product with id 999. This simulates the real service
        // behaviour when a product does not exist.
        when(productService.getProductById(999L))
                .thenThrow(new ProductNotFoundException(999L));

        // Act & Assert: the request should produce a 404 response whose body
        // matches the ErrorResponse structure your GlobalExceptionHandler builds.
        mockMvc.perform(get("/api/products/999")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())       // HTTP 404
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                // verify each field of the ErrorResponse JSON
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("Product not found with id: 999"))
                .andExpect(jsonPath("$.path").value("/api/products/999"))
                // timestamp must exist but we don't assert its exact value
                // because it changes every time the test runs
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void createProduct_withValidRequest_shouldReturn201WithProductBody() throws Exception {
        CreateProductRequest request = CreateProductRequest.builder()
                .name("Laptop Pro 15")
                .description("Developer laptop")
                .price(new BigDecimal("1299.99"))
                .stock(25)
                .categoryId(1L)
                .build();

        when(productService.createProduct(any(CreateProductRequest.class)))
                .thenReturn(sampleProductDTO);

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())  // 201, not 200
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Laptop Pro 15"));
    }

    @Test
    void createProduct_withInvalidRequest_shouldReturn400() throws Exception {
        CreateProductRequest invalidRequest = CreateProductRequest.builder()
                .name("")            // violates @NotBlank
                .price(new BigDecimal("-50"))  // violates @Positive
                .stock(10)
                .categoryId(1L)
                .build();

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())  // 400
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"));
    }
}