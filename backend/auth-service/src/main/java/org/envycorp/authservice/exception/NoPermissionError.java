package org.envycorp.authservice.exception;

public class NoPermissionError extends RuntimeException {
    public NoPermissionError(String message) {
        super(message);
    }
}
