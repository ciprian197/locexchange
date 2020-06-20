package com.ubb.locexchange.exception.error;

import lombok.AllArgsConstructor;
import lombok.Getter;


@Getter
@AllArgsConstructor
public enum MissionErrorType implements ErrorType {

    NOT_FOUND(Value.NOT_FOUND, "Mission not found!");

    private final String code;
    private final String description;

    private class Value {

        private static final String PREFIX = "mission_";

        public static final String NOT_FOUND = PREFIX + "not_found";

    }

}
