package com.ubb.locexchange.domain;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@Builder
@Document
@NoArgsConstructor
@AllArgsConstructor
public class Address {

    @Id
    private String id;
    private String country;
    private String city;
    private String street;
    private int number;
    private int apartmentNumber;

}
