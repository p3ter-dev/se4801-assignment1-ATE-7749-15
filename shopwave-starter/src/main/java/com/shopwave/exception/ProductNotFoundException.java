// Name: Peter Kinfe
// ID: ATE/7749/15
package com.shopwave.exception;

public class ProductNotFoundException extends RuntimeException {
    public ProductNotFoundException(Long id) {
        super("Product not found with id: " + id);
    }
}
