package com.ubb.locexchange.exception;


import lombok.Getter;

import java.util.Optional;

@Getter
public abstract class RestRuntimeException extends RuntimeException {

    private static final long serialVersionUID = -8081351967598635610L;

    private final ErrorType errorType;

    RestRuntimeException(final ErrorType errorType, final String message) {
        this(errorType, message, null);
    }

    RestRuntimeException(final ErrorType errorType, final String message, final Throwable cause) {
        super(message, cause);
        this.errorType = errorType;
    }

    public String getMessage() {
        return Optional.ofNullable(this.errorType)
                .map((e) -> String.format("Code: %s. Message: %s", e.getCode(), super.getMessage()))
                .orElseGet(super::getMessage);
    }

}

