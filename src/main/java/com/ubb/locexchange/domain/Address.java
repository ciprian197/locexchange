package com.ubb.locexchange.domain;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Address {

    private String country;
    private String city;
    private String street;
    private int number;
    private int apartmentNumber;

}
