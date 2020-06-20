package com.ubb.locexchange.exception;

import com.ubb.locexchange.exception.error.ErrorType;

public class ResourceNotFoundExcetion extends RestRuntimeException {

    public ResourceNotFoundExcetion(final ErrorType errorType, final String message) {
        super(errorType, message);
    }

}
