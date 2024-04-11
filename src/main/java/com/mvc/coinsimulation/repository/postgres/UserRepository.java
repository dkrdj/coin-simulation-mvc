package com.mvc.coinsimulation.repository.postgres;

import com.mvc.coinsimulation.entity.User;
import com.mvc.coinsimulation.repository.postgres.custom.UserCustomRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long>, UserCustomRepository {

    List<User> findTop10ByOrderByCashDesc();
}
