package com.ubb.locexchange.dto;


import com.ubb.locexchange.domain.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LocationExchangeDto {

    private String username;

    private Role role;

    private String consigneeId;

    @Valid
    private GeoPointDto location;

}
