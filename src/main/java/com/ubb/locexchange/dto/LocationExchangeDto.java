package com.ubb.locexchange.dto;


import com.ubb.locexchange.domain.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LocationExchangeDto implements Serializable {

    private static final long serialVersionUID = -5223740492908559431L;

    private String username;

    private Role role;

    private String consigneeId;

    @Valid
    private GeoPointDto location;

}