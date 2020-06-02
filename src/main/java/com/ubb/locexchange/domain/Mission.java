package com.ubb.locexchange.domain;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "missions")
public class Mission {

    @Id
    private String id;

    private String providerId;

    @NotNull
    private String clientId;

    private MissionStatus status;

}
