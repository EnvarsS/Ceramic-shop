package org.envycorp.userservice.exception.user;

public class EmailIsAlreadyTakenException extends RuntimeException {
    public EmailIsAlreadyTakenException(String message) {
        super(message);
    }
}
