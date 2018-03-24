package com.ubb.locexchange.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddressDto implements Serializable {

    private static final long serialVersionUID = 1000634227505509358L;
    @NotNull
    private String country;
    @NotNull
    private String city;
    @NotNull
    private String street;
    @NotNull
    private int number;
    private int apartmentNumber;

}
