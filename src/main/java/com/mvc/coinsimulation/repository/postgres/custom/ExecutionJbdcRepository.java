package com.mvc.coinsimulation.repository.postgres.custom;

import com.mvc.coinsimulation.entity.Execution;

import java.util.List;

public interface ExecutionJbdcRepository {
    void bulkInsert(List<Execution> executions);
}
