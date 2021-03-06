package com.ubb.locexchange.domain;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexType;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexed;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotNull;

import static com.ubb.locexchange.domain.Role.PROVIDER;


@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "users")
public class User {

    @Id
    private String id;

    private String webSessionId;

    @NotNull
    @Indexed(unique = true)
    private String username;

    @NotNull
    private String firstName;

    @NotNull
    private String lastName;

    @Builder.Default
    private Role role = PROVIDER;

    @NotNull
    private Address address;

    @GeoSpatialIndexed(type = GeoSpatialIndexType.GEO_2DSPHERE)
    private GeoJsonPoint location;

    @Builder.Default
    private UserStatus status = UserStatus.DISCONNECTED;

    @Getter
    @AllArgsConstructor
    public enum Property {

        ID("id"),
        FIRST_NAME("firstName"),
        LAST_NAME("lastName"),
        ROLE("role"),
        STATUS("status"),
        LOCATION("location");

        private final String value;

    }

}
