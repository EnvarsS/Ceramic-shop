package org.envycorp.authservice.exception;

public class UsernameNotExists extends RuntimeException{
    public UsernameNotExists(String message) {
        super(message);
    }
}
