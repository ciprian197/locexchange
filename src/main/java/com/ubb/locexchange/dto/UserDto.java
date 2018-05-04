package com.ubb.locexchange.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.ubb.locexchange.domain.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

import javax.validation.constraints.Pattern;
import java.io.Serializable;

import static com.ubb.locexchange.util.UserUtils.NAME_PATTERN;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserDto implements Serializable {

    private static final long serialVersionUID = 2253559000626589487L;

    @Id
    private String id;

    @Pattern(regexp = NAME_PATTERN, message = "{firstName.invalid}")
    private String firstName;

    @Pattern(regexp = NAME_PATTERN, message = "{lastName.invalid}")
    private String lastName;

    private Role role;

    private AddressDto address;

    private GeoPointDto location;

}
