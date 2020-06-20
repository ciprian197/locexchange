package com.ubb.locexchange.exception;

import com.ubb.locexchange.exception.error.ErrorType;

public class ConnectionException extends RestRuntimeException {

    public ConnectionException(final ErrorType errorType, final String message) {
        super(errorType, message);
    }

}
