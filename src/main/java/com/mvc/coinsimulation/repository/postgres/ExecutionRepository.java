package com.mvc.coinsimulation.repository.postgres;

import com.mvc.coinsimulation.entity.Execution;
import com.mvc.coinsimulation.repository.postgres.custom.ExecutionJbdcRepository;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ExecutionRepository extends JpaRepository<Execution, Long>, ExecutionJbdcRepository {
    List<Execution> findTop10ByUserId(Long userId);
}
