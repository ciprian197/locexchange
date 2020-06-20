package com.ubb.locexchange.exception;

import com.ubb.locexchange.exception.error.GeneralErrorType;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;

@RestControllerAdvice
public class ExceptionHandling {

    @ExceptionHandler
    private ResponseEntity<ErrorView> handleException(final WebExchangeBindException ex) {
        final String errorMessage = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(FieldError::getDefaultMessage)
                .reduce((a, b) -> a + "\n" + b)
                .orElse("Cannot display errors");
        return new ResponseEntity<>(new ErrorView(GeneralErrorType.VALIDATION_ERROR, errorMessage), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    private ResponseEntity<ErrorView> handleException(final RestRuntimeException ex) {

        return new ResponseEntity<>(new ErrorView(ex.getErrorType(), ex.getMessage()), HttpStatus.NOT_FOUND);

    }

    private ResponseEntity<ErrorView> handleException(final InvalidDataException ex) {

        return new ResponseEntity<>(new ErrorView(ex.getErrorType(), ex.getMessage()), HttpStatus.UNPROCESSABLE_ENTITY);

    }

}
