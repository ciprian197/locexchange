package com.ubb.locexchange.mapper;

import com.ubb.locexchange.domain.User;
import com.ubb.locexchange.dto.UserDto;
import org.springframework.data.geo.GeoResult;
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
                .username(dto.getUsername())
                .firstName(dto.getFirstName())
                .lastName(dto.getLastName())
                .address(addressMapper.toEntity(dto.getAddress()))
                .location(geoPointMapper.toEntity(dto.getLocation())).build();
    }

    public UserDto toDto(final User entity) {
        return UserDto.builder()
                .id(entity.getId())
                .webSessionId(entity.getWebSessionId())
                .username(entity.getUsername())
                .firstName(entity.getFirstName())
                .lastName(entity.getLastName())
                .role(entity.getRole())
                .address(addressMapper.toDto(entity.getAddress()))
                .location(geoPointMapper.toDto(entity.getLocation())).build();
    }

//    public UserDto toDto(final GeoResult<User> geoResult) {
//        final User user = geoResult.getContent();
//        return toDto(user);
//    }

}
