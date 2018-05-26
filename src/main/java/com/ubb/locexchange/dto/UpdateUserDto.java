package com.ubb.locexchange.dto;

import com.ubb.locexchange.domain.UserStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserDto implements Serializable {

    private static final long serialVersionUID = -5582782229747059055L;

    private GeoPointDto location;
    private UserStatus userStatus;
    private String webSessionId;

}
