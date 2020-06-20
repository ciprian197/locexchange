package com.ubb.locexchange.service.user;

import com.ubb.locexchange.domain.User;
import com.ubb.locexchange.exception.error.GeneralErrorType;
import com.ubb.locexchange.exception.InvalidDataException;
import org.springframework.stereotype.Component;

import static com.ubb.locexchange.domain.Role.PROVIDER;

@Component
public class UserValidator {

    public void validateUserForLocationUpdate(final User user) {
        if (!PROVIDER.equals(user.getRole())) {
            throw new InvalidDataException(GeneralErrorType.CAN_NOT_UPDATE_LOCATION_FOR_USER,
                    "Can not update location for users with role " + user.getRole());
        }
    }

}
