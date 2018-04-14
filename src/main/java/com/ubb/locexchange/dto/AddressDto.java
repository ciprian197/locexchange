package com.ubb.locexchange.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.io.Serializable;

import static com.ubb.locexchange.util.UserUtils.PLACE_NAME_PATTERN;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddressDto implements Serializable {

    private static final long serialVersionUID = 1000634227505509358L;

    @NotBlank
    @Pattern(regexp = PLACE_NAME_PATTERN, message = "{country.invalid}")
    private String country;

    @NotBlank
    @Pattern(regexp = PLACE_NAME_PATTERN, message = "{city.invalid}")
    private String city;

    @Pattern(regexp = PLACE_NAME_PATTERN, message = "{street.invalid}")
    private String street;

    @NotNull
    private int number;

    private int apartmentNumber;

}
