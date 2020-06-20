package com.ubb.locexchange.service.distance;

import com.google.maps.DistanceMatrixApi;
import com.google.maps.GeoApiContext;
import com.google.maps.model.DistanceMatrix;
import com.google.maps.model.DistanceMatrixElement;
import com.ubb.locexchange.domain.User;
import com.ubb.locexchange.dto.GeoPointDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.stereotype.Service;
import reactor.core.Exceptions;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
class DistanceExternalServiceImpl implements DistanceExternalService {

    private final String googleApiKey;

    public DistanceExternalServiceImpl(@Value("${google-maps.api.key}") final String googleApiKey) {
        this.googleApiKey = googleApiKey;
    }

    @Override
    public User getClosestUser(final List<User> users, final GeoPointDto point) {
        final GeoApiContext geoApiContext = new GeoApiContext.Builder()
                .apiKey(googleApiKey).build();

        try {
            return users.get(findClosest(DistanceMatrixApi.getDistanceMatrix(geoApiContext, getOrigins(users),
                    new String[]{geoPointDtoAsString(point)}).await()));
        } catch (final Exception e) {
            // Wrap caught exception into a RuntimeException to be able to handle it inside of the reactive chain
            throw Exceptions.propagate(e);
        }
    }

    private int findClosest(final DistanceMatrix distanceMatrix) {
        long minimumDuration = Long.MAX_VALUE;
        int position = -1;
        for (int counter = 0; counter < distanceMatrix.rows.length; counter++) {
            for (final DistanceMatrixElement de : distanceMatrix.rows[counter].elements) {
                final long duration = de.duration.inSeconds;
                if (duration < minimumDuration) {
                    minimumDuration = duration;
                    position = counter;
                }
            }
        }
        return position;
    }

    private String[] getOrigins(final List<User> users) {
        return users.stream()
                .map(user -> geoJsonPointAsString(user.getLocation()))
                .collect(Collectors.toList())
                .toArray(new String[users.size()]);
    }

    // Here we have y before x because y is the latitude and x the longitude
    private String geoJsonPointAsString(final GeoJsonPoint geoJsonPoint) {
        return geoJsonPoint.getY() + "," + geoJsonPoint.getX();
    }

    // Here we have y before x because y is the latitude and x the longitude
    private String geoPointDtoAsString(final GeoPointDto geoPointDto) {
        return geoPointDto.getY() + "," + geoPointDto.getX();
    }

}
