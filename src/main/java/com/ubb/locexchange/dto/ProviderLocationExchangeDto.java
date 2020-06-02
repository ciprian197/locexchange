package com.ubb.locexchange.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProviderLocationExchangeDto {

    private String userId;

    @Valid
    private GeoPointDto location;

}
