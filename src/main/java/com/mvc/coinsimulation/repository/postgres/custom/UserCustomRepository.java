package com.mvc.coinsimulation.repository.postgres.custom;

import com.mvc.coinsimulation.entity.User;

import java.util.Optional;

public interface UserCustomRepository {

    Optional<User> findByIdForUpdate(Long id);
}
