package org.envycorp.userservice.exception.user;

public class InvalidUserLoginDataException extends RuntimeException {
    public InvalidUserLoginDataException(String message) {
        super(message);
    }
}
