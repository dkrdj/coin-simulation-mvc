package com.mvc.coinsimulation.repository.postgres;

import com.mvc.coinsimulation.entity.Execution;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ExecutionRepository extends JpaRepository<Execution, Long> {
    List<Execution> findTop10ByUserId(Long userId);
}
