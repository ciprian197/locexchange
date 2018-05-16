package com.ubb.locexchange.controller.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.UtilityClass;

@Getter
@AllArgsConstructor
public enum ErrorType {

    CANNOT_FIND_AVAILABLE_PROVIDERS(Value.CANNOT_FIND_AVAILABLE_PROVIDERS,
            "Can not find available providers for the given location");

    private String code;
    private String description;

    @UtilityClass
    public class Value {
        private static final String PREFIX = "locexchange.";

        private static final String CANNOT_FIND_AVAILABLE_PROVIDERS = PREFIX + "1000";
    }

}
