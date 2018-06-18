package com.ubb.locexchange.factory;

import com.ubb.locexchange.domain.Role;
import com.ubb.locexchange.domain.User;
import com.ubb.locexchange.domain.UserStatus;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import reactor.core.publisher.Flux;

public class UserFactory {

    public static Flux<User> generateProviders() {
        return Flux.just(1, 2, 3)
                .map(n -> userBuilder("ciprian" + n, Role.PROVIDER).build());
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
