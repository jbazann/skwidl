package dev.jbazann.skwidl.products.product.exceptions;

public class InsufficientStockException extends RuntimeException {
    public InsufficientStockException() {
        super();
    }
    public InsufficientStockException(String message) {
        super(message);
    }
}
