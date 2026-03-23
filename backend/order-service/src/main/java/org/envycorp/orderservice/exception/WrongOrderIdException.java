package org.envycorp.orderservice.exception;

public class WrongOrderIdException extends RuntimeException {
    public WrongOrderIdException(String message) {
        super(message);
    }
}
