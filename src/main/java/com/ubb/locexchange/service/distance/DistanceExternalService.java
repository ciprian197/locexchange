package com.ubb.locexchange.service.distance;

import com.ubb.locexchange.domain.User;
import com.ubb.locexchange.dto.GeoPointDto;

import java.util.List;

public interface DistanceExternalService {

    User getClosestUser(List<User> users, GeoPointDto point);

}
