package com.ubb.locexchange.controller.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;

@RestControllerAdvice
public class ExceptionHandling {

    @ExceptionHandler
    private ResponseEntity<String> handleException(final WebExchangeBindException ex) {
        String errorMessage = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(FieldError::getDefaultMessage)
                .reduce((a, b) -> a + "\n" + b)
                .orElse("Cannot display errors");
        return new ResponseEntity<>(errorMessage, HttpStatus.BAD_REQUEST);
    }

}
