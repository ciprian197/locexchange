package com.ubb.locexchange.repository;

import com.ubb.locexchange.domain.User;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface UserRepository extends ReactiveMongoRepository<User, String> {
}
