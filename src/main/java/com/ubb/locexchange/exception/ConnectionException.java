package com.ubb.locexchange.exception;

public class ConnectionException extends RestRuntimeException  {

    private static final long serialVersionUID = -846494365629431441L;

    public ConnectionException(final ErrorType errorType, final String message) {
        super(errorType, message);
    }

}
