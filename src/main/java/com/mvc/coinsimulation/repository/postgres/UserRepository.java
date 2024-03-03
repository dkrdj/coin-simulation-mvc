package com.mvc.coinsimulation.repository.postgres;

import com.mvc.coinsimulation.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByProviderId(Long providerId);

    //    @Query("select users.* " +
//            "from users " +
//            "where users.id = :id " +
//            "for update")
//    Optional<User> findByIdForUpdate(Long id);

    List<User> findTop10ByOrderByCashDesc();
}
