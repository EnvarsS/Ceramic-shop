package org.envycorp.authservice.controller;

import org.envycorp.authservice.exception.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class ServiceExceptionHandler {
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleIllegalArgumentException(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult().getFieldErrors().forEach(
                error -> errors.put(error.getField(), error.getDefaultMessage())
        );

        return ResponseEntity.badRequest().body(errors);
    }

    @ExceptionHandler({
            IncorrectPasswordException.class,
            UsernameIsAlreadyTaken.class,
            UsernameNotExists.class,
            NoPermissionError.class,
            EmailIsAlreadyTaken.class,
            UserNotFoundException.class,
            NoPermissionError.class
    })
    public ResponseEntity<Map<String, String>> handleCustomExceptions(RuntimeException ex) {
        Map<String, String> error = new HashMap<>();
        error.put("error", ex.getMessage());
        return ResponseEntity.badRequest().body(error);
    }
}
