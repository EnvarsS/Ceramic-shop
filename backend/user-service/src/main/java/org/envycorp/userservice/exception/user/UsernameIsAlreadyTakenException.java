package org.envycorp.userservice.exception.user;

public class UsernameIsAlreadyTakenException extends RuntimeException {
    public UsernameIsAlreadyTakenException(String message) {
        super(message);
    }
}
