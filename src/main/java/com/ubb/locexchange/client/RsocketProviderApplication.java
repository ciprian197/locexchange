package com.ubb.locexchange.client;

import com.ubb.locexchange.dto.GeoPointDto;
import com.ubb.locexchange.dto.UserDto;
import com.ubb.locexchange.util.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.http.codec.json.Jackson2JsonEncoder;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.messaging.rsocket.RSocketStrategies;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Arrays;

@Slf4j
public class RsocketProviderApplication {

    private static final GeoPointDto geoPoint = GeoPointDto.builder()
            .x(26.854895)
            .y(47.43484).build();
    private static final String USER_ID = "5ed534f861fd745db23788d5";

    public static void main(final String[] args) throws InterruptedException {
        final Mono<RSocketRequester> rSocketRequester = getRSocketRequester();

        rSocketRequester
                .flatMap(req -> req.route("provider.{userId}", USER_ID)
                        .data(Flux.fromIterable(Arrays.asList(geoPoint, geoPoint, geoPoint, geoPoint)))
                        .retrieveFlux(String.class)
                        .map(payload -> JsonUtils.getObjectFromString(payload, UserDto.class))
                        .next())
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
                .connectTcp("localhost", 7000);
    }

}
