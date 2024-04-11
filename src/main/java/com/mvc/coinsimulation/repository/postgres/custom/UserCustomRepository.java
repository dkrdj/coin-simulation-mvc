package com.mvc.coinsimulation.repository.postgres.custom;

import com.mvc.coinsimulation.entity.User;

import java.util.List;
import java.util.Optional;

public interface UserCustomRepository {

    Optional<User> findByIdForUpdate(Long id);

    List<User> findAllByIdForUpdate(List<Long> userIds);
}
