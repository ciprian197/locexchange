package com.ubb.locexchange.factory;

import com.ubb.locexchange.domain.Role;
import com.ubb.locexchange.domain.User;
import com.ubb.locexchange.domain.UserStatus;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class UserFactory {

    public static List<User> generateProviders() {
        return Stream.of(1, 2, 3)
                .map(n -> userBuilder("ciprian" + n, Role.PROVIDER).build())
                .collect(Collectors.toList());
    }

    public static User generateProvider() {
        return userBuilder("ciprian", Role.PROVIDER).build();
    }

    private static User.UserBuilder userBuilder(final String username, final Role role) {
        return User.builder()
                .username(username)
                .role(role)
                .userStatus(UserStatus.CONNECTED)
                .firstName("Ciprian")
                .lastName("Popescu")
                .webSessionId("1234567")
                .location(new GeoJsonPoint(23.456, 43.234));
    }

}
