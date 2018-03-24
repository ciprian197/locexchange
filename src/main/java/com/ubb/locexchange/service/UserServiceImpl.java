package com.ubb.locexchange.service;

import com.ubb.locexchange.domain.User;
import com.ubb.locexchange.dto.UserDto;
import com.ubb.locexchange.mapper.UserMapper;
import com.ubb.locexchange.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final UserRepository userRepository;

    public UserServiceImpl(final UserMapper userMapper, final UserRepository userRepository) {
        this.userMapper = userMapper;
        this.userRepository = userRepository;
    }

    @Override
    public Mono<UserDto> addUser(final UserDto userDto) {
        final User user = userMapper.toEntity(userDto);
        return userRepository.save(user)
                .map(userMapper::toDto)
                .doOnNext(u -> log.info("User created: {}", u));
    }

}
