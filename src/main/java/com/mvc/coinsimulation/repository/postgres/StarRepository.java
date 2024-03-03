package com.mvc.coinsimulation.repository.postgres;

import com.mvc.coinsimulation.entity.Star;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StarRepository extends JpaRepository<Star, Long> {
    List<Star> findByUserId(Long userId);

    Optional<Void> deleteByUserIdAndCode(Long userId, String code);
}
