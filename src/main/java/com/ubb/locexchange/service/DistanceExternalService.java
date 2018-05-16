package com.ubb.locexchange.service;

import com.ubb.locexchange.dto.GeoPointDto;
import com.ubb.locexchange.dto.UserDto;

import java.util.List;

public interface DistanceExternalService {

    UserDto getClosestUser(List<UserDto> users, GeoPointDto point);

}
