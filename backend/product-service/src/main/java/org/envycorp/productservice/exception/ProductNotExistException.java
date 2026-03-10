package org.envycorp.productservice.exception;

public class ProductNotExistException extends RuntimeException {
    public ProductNotExistException(String message) {
        super(message);
    }
}
