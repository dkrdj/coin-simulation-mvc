package com.mvc.coinsimulation.repository.postgres;

import com.mvc.coinsimulation.entity.Star;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StarRepository extends JpaRepository<Star, Long> {
    List<Star> findByUserId(Long userId);

    void deleteByUserIdAndCode(Long userId, String code);
}
