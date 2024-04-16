package com.mvc.coinsimulation.repository.postgres;

import com.mvc.coinsimulation.entity.User;
import com.mvc.coinsimulation.repository.postgres.custom.UserCustomRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long>, UserCustomRepository {

    List<User> findTop10ByOrderByCashDesc();

    Optional<User> findByProviderId(Long providerId);
}
