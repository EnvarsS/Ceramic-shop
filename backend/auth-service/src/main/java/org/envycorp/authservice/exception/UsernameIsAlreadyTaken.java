package org.envycorp.authservice.exception;

public class UsernameIsAlreadyTaken extends RuntimeException{
    public UsernameIsAlreadyTaken(String message) {
        super(message);
    }
}
