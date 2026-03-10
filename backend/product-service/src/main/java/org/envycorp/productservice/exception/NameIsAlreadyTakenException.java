package org.envycorp.productservice.exception;

public class NameIsAlreadyTakenException extends RuntimeException {
    public NameIsAlreadyTakenException(String msg) {
        super(msg);
    }
}
