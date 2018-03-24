package com.ubb.locexchange.mapper;

import com.ubb.locexchange.domain.User;
import com.ubb.locexchange.dto.UserDto;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    private final AddressMapper addressMapper;
    private final GeoPointMapper geoPointMapper;

    public UserMapper(final AddressMapper addressMapper, final GeoPointMapper geoPointMapper) {
        this.addressMapper = addressMapper;
        this.geoPointMapper = geoPointMapper;
    }

    public User toEntity(final UserDto dto) {
        if (dto == null) {
            return null;
        }

        return User.builder()
                .firstName(dto.getFirstName())
                .lastName(dto.getLastName())
                .role(dto.getRole())
                .address(addressMapper.toEntity(dto.getAddress()))
                .location(geoPointMapper.toEntity(dto.getLocation())).build();
    }

    public UserDto toDto(final User entity) {
        return UserDto.builder()
                .id(entity.getId())
                .firstName(entity.getFirstName())
                .lastName(entity.getLastName())
                .role(entity.getRole())
                .address(addressMapper.toDto(entity.getAddress()))
                .location(geoPointMapper.toDto(entity.getLocation())).build();
    }

}
