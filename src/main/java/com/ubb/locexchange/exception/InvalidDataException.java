package com.ubb.locexchange.exception;

import com.ubb.locexchange.exception.error.ErrorType;
import com.ubb.locexchange.exception.error.GeneralErrorType;

public class InvalidDataException extends RestRuntimeException {

    public InvalidDataException(final ErrorType errorType, final String message) {
        super(errorType, message);
    }

}
