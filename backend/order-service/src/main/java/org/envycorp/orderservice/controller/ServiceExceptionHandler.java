package org.envycorp.orderservice.controller;

import org.envycorp.orderservice.exception.WrongOrderIdException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ServiceExceptionHandler {
    @ExceptionHandler(WrongOrderIdException.class)
    public ResponseEntity<String> exceptionHandler(WrongOrderIdException exception) {
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.BAD_REQUEST);
    }
}
