package org.envycorp.orderservice.exception;

public class OrderIsNotExisted extends RuntimeException {
    public OrderIsNotExisted(String message) {
        super(message);
    }
}
