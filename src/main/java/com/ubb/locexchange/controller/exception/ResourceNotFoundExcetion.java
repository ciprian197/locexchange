package com.ubb.locexchange.controller.exception;

public class ResourceNotFoundExcetion extends RestRuntimeException{

    private static final long serialVersionUID = -1913574523460237681L;

    public ResourceNotFoundExcetion(final ErrorType errorType, final String message) {
        super(errorType,message);
    }

}
