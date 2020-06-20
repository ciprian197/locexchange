package com.ubb.locexchange.client;

import com.ubb.locexchange.dto.GeoPointDto;
import com.ubb.locexchange.dto.mission.MissionDto;
import com.ubb.locexchange.util.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.http.codec.json.Jackson2JsonEncoder;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.messaging.rsocket.RSocketStrategies;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.time.Duration;

@Slf4j
public class RSocketProviderApplication {

    private static final GeoPointDto geoPoint = GeoPointDto.builder()
            .x(26.854895)
            .y(47.43484).build();
    private static final GeoPointDto geoPoint1 = GeoPointDto.builder()
            .x(26.854895)
            .y(47.434848).build();
    private static final GeoPointDto geoPoint2 = GeoPointDto.builder()
            .x(26.854896)
            .y(47.434847).build();
    private static final GeoPointDto geoPoint3 = GeoPointDto.builder()
            .x(26.854897)
            .y(47.434845).build();
    private static final String PROVIDER_ID = "5ed534f861fd745db23788d5";

    public static void main(final String[] args) throws InterruptedException {
        final Mono<RSocketRequester> rSocketRequester = getRSocketRequester();

        final Flux<GeoPointDto> locations = getDefaultLocations();

        rSocketRequester
                .flatMap(req -> req.route("provider.{providerId}", PROVIDER_ID)
                        .data(locations)
                        .retrieveFlux(String.class)
                        .map(payload -> JsonUtils.getObjectFromString(payload, MissionDto.class))
                        .doOnNext(RSocketProviderApplication::logClientLocation)
                        .collectList())
                .subscribe(e -> log.info("Received response"));

        Thread.sleep(300000);
    }

    private static Mono<RSocketRequester> getRSocketRequester() {
        final RSocketStrategies rSocketStrategies = RSocketStrategies.builder()
                .encoder(new Jackson2JsonEncoder())
                .decoder(new Jackson2JsonDecoder()).build();

        return RSocketRequester.builder()
                .rsocketStrategies(rSocketStrategies)
                .dataMimeType(MediaType.APPLICATION_JSON)
                .connectWebSocket(URI.create("http://localhost:7000/rsocket"));
    }

    private static Flux<GeoPointDto> getDefaultLocations() {
        final Mono<GeoPointDto> firstLocation = Mono.just(geoPoint);
        final Mono<GeoPointDto> secondLocation = Mono.just(geoPoint1).delayElement(Duration.ofSeconds(30));
        final Mono<GeoPointDto> thirdLocation = Mono.just(geoPoint2).delayElement(Duration.ofSeconds(30));
        final Mono<GeoPointDto> fourthLocation = Mono.just(geoPoint3).delayElement(Duration.ofSeconds(30));

        return Flux.concat(firstLocation, secondLocation, thirdLocation, fourthLocation)
                .doOnNext(location -> log.info("Sending new location information {}", location));
    }

    private static void logClientLocation(final MissionDto missionDto) {
        final GeoPointDto clientLocation = missionDto.getClientLocation();
        log.info("Client location(x={},y={})", clientLocation == null ? null : clientLocation.getX(),
                clientLocation == null ? null : clientLocation.getY());
    }

}
