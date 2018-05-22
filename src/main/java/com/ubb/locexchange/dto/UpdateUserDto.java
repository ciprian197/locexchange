package com.ubb.locexchange.dto;

import com.ubb.locexchange.domain.UserStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserDto {

    private GeoPointDto location;
    private UserStatus userStatus;

}
