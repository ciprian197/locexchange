package com.ubb.locexchange.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.ubb.locexchange.domain.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

import javax.validation.constraints.NotNull;
import java.io.Serializable;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserDto implements Serializable {

    private static final long serialVersionUID = 2253559000626589487L;

    @Id
    private String id;
    @NotNull
    private String firstName;
    @NotNull
    private String lastName;
    private Role role;
    @NotNull
    private AddressDto address;
    private GeoPointDto location;

}
