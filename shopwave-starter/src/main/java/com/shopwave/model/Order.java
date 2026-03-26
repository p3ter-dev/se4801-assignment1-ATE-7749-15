// Name: Peter Kinfe
// ID: ATE/7749/15
package com.shopwave.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String orderNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal totalAmount;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<OrderItem> items = new ArrayList<>();

    public void addItem(Product product, int quantity) {
        // Create a new OrderItem and set all its required fields
        OrderItem item = OrderItem.builder()
                .order(this)                  // link back to this order
                .product(product)             // which product is being ordered
                .quantity(quantity)           // how many units
                .unitPrice(product.getPrice()) // snapshot the current price
                .build();

        this.items.add(item);

        BigDecimal itemTotal = product.getPrice()
                .multiply(BigDecimal.valueOf(quantity));

        if (this.totalAmount == null) {
            this.totalAmount = BigDecimal.ZERO;
        }
        this.totalAmount = this.totalAmount.add(itemTotal);
    }
}