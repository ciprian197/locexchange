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
public class RSocketClientApplication {

    private static final GeoPointDto geoPoint = GeoPointDto.builder()
            .x(26.854895)
            .y(47.43484).build();
    private static final GeoPointDto geoPoint2 = GeoPointDto.builder()
            .x(26.854896)
            .y(47.43484).build();
    private static final GeoPointDto geoPoint3 = GeoPointDto.builder()
            .x(26.854897)
            .y(47.43484).build();

    private static final String CLIENT_ID = "5ed53b7461fd745db23788d6";

    public static void main(final String[] args) throws InterruptedException {
        final Mono<RSocketRequester> rSocketRequester = getRSocketRequester();

        final Flux<GeoPointDto> locations = getDefaultLocations();

        rSocketRequester
                .flatMap(req -> req.route("client.{clientId}", CLIENT_ID)
                        .data(locations)
                        .retrieveFlux(String.class)
                        .map(payload -> JsonUtils.getObjectFromString(payload, MissionDto.class))
                        .doOnNext(missionDto -> log.info("Provider location(x={},y={})", missionDto.getProviderLocation().getX(),
                                missionDto.getProviderLocation().getY()))
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
        final Mono<GeoPointDto> secondLocation = Mono.just(geoPoint2).delayElement(Duration.ofSeconds(25));
        final Mono<GeoPointDto> thirdLocation = Mono.just(geoPoint3).delayElement(Duration.ofSeconds(25));

        return Flux.concat(firstLocation, secondLocation, thirdLocation)
                .doOnNext(location -> log.info("Sending location(x={}, y={})", location.getX(), location.getY()));
    }

}
