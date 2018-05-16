package com.ubb.locexchange.exception;

public class InvalidDataException extends RestRuntimeException {

    private static final long serialVersionUID = 1428297523623293134L;

    public InvalidDataException(final ErrorType errorType, final String message) {
        super(errorType, message);
    }

}
