package com.ubb.locexchange.mapper;

import com.ubb.locexchange.domain.Address;
import com.ubb.locexchange.dto.AddressDto;
import org.springframework.stereotype.Component;

@Component
public class AddressMapper {

    public Address toEntity(final AddressDto addressDto) {
        if (addressDto == null) {
            return null;
        }

        return Address.builder()
                .country(addressDto.getCountry())
                .city(addressDto.getCity())
                .street(addressDto.getStreet())
                .number(addressDto.getNumber())
                .apartmentNumber(addressDto.getApartmentNumber()).build();
    }

    public AddressDto toDto(final Address entity) {
        if (entity == null) {
            return null;
        }

        return AddressDto.builder()
                .country(entity.getCountry())
                .city(entity.getCity())
                .street(entity.getStreet())
                .number(entity.getNumber())
                .apartmentNumber(entity.getApartmentNumber()).build();
    }

}
