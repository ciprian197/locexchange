package com.ubb.locexchange.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import reactor.core.Exceptions;

import java.io.IOException;

public final class JsonUtils {

    private static final ObjectMapper json = new ObjectMapper();

    public static <T> T getObjectFromString(final String payload, final Class<T> tClass) {
        try {
            return json.readValue(payload, tClass);
        } catch (final IOException e) {
            throw Exceptions.propagate(e);
        }
    }

    public static <T> String convertObjectToJson(final T value) {
        try {
            return json.writeValueAsString(value);
        } catch (final IOException e) {
            throw Exceptions.propagate(e);
        }
    }

}
