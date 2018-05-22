package com.ubb.locexchange.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.ubb.locexchange.domain.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;
import org.springframework.data.annotation.Id;

import javax.validation.constraints.Pattern;
import java.io.Serializable;

import static com.ubb.locexchange.util.UserUtils.NAME_PATTERN;
import static com.ubb.locexchange.util.UserUtils.USERNAME_PATTERN;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserDto implements Serializable {

    private static final long serialVersionUID = 2253559000626589487L;

    @Id
    private String id;

    @Length(min = 4, max = 15, message = "{username.invalid.length}")
    @Pattern(regexp = USERNAME_PATTERN, message = "{username.invalid")
    private String username;

    @Pattern(regexp = NAME_PATTERN, message = "{firstName.invalid}")
    private String firstName;

    @Pattern(regexp = NAME_PATTERN, message = "{lastName.invalid}")
    private String lastName;

    private Role role;

    private AddressDto address;

    private GeoPointDto location;

}
