package com.ubb.locexchange.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.UtilityClass;

@Getter
@AllArgsConstructor
public enum ErrorType {

    VALIDATION_ERROR(Value.VALIDATION_ERROR,
            "Error occured while validating input data"),
    CAN_NOT_FIND_AVAILABLE_PROVIDERS(Value.CAN_NOT_FIND_AVAILABLE_PROVIDERS,
            "Can not find available providers for the given location"),
    USER_DOES_NOT_EXIST(Value.USER_DOES_NOT_EXIST, "User does not exist"),
    CAN_NOT_UPDATE_LOCATION_FOR_USER(Value.CAN_NOT_UPDATE_LOCATION_FOR_USER, "Can not update location for user"),
    SESSION_DOES_NOT_EXIST(Value.SESSION_DOES_NOT_EXIST, "Session does not exist");

    private String code;
    private String description;

    @UtilityClass
    public class Value {

        private static final String PREFIX = "locexchange.";

        private static final String VALIDATION_ERROR = PREFIX + "1000";
        private static final String CAN_NOT_FIND_AVAILABLE_PROVIDERS = PREFIX + "1001";
        private static final String USER_DOES_NOT_EXIST = PREFIX + "1002";
        private static final String CAN_NOT_UPDATE_LOCATION_FOR_USER = PREFIX + "1003";
        private static final String SESSION_DOES_NOT_EXIST = PREFIX + "1003";

    }

}
