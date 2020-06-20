package com.ubb.locexchange.exception.error;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.UtilityClass;

@Getter
@AllArgsConstructor
public enum GeneralErrorType implements ErrorType {

    VALIDATION_ERROR(Value.VALIDATION_ERROR, "Error occured while validating input data"),
    CAN_NOT_FIND_AVAILABLE_PROVIDERS(Value.CAN_NOT_FIND_AVAILABLE_PROVIDERS,
            "Can not find available providers for the given location"),
    USER_NOT_FOUND(Value.USER_NOT_FOUND, "User does not exist"),
    CAN_NOT_UPDATE_LOCATION_FOR_USER(Value.CAN_NOT_UPDATE_LOCATION_FOR_USER, "Can not update location for user"),
    SESSION_NOT_FOUND(Value.SESSION_NOT_FOUND, "Session does not exist");

    private String code;
    private String description;

    @UtilityClass
    public class Value {

        private static final String PREFIX = "locexchange_";

        private static final String VALIDATION_ERROR = PREFIX + "validation_error";
        private static final String CAN_NOT_FIND_AVAILABLE_PROVIDERS = PREFIX + "unable_to_find_available_providers";
        private static final String USER_NOT_FOUND = PREFIX + "user_not_found";
        private static final String CAN_NOT_UPDATE_LOCATION_FOR_USER = PREFIX + "unable_to_update_location_for_user";
        private static final String SESSION_NOT_FOUND = PREFIX + "session_not_found";

    }

}
