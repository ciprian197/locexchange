package com.ubb.locexchange.dto.mission;

import com.ubb.locexchange.domain.MissionStatus;
import com.ubb.locexchange.dto.GeoPointDto;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MissionDto {

    private String id;
    private String providerId;
    private String clientId;
    private GeoPointDto providerLocation;
    private GeoPointDto clientLocation;
    private MissionStatus missionStatus;

}
