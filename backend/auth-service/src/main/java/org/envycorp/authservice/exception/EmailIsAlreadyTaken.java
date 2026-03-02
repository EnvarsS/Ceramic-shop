package org.envycorp.authservice.exception;

public class EmailIsAlreadyTaken extends RuntimeException {
    public EmailIsAlreadyTaken(String message) {
        super(message);
    }
}
