package com.mvc.coinsimulation.repository.postgres.custom;

import com.mvc.coinsimulation.entity.Execution;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class ExecutionJbdcRepositoryImpl implements ExecutionJbdcRepository {

    private final JdbcTemplate jdbcTemplate;

    @Override
    @Transactional
    public void bulkInsert(List<Execution> executions) {
        String sql = "INSERT INTO EXECUTION" +
                "(user_id, gubun, amount, code, price, total_price, date_time, sequential_id) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                Execution execution = executions.get(i);
                ps.setLong(1, execution.getUserId());
                ps.setString(2, execution.getGubun().getValue());
                ps.setBigDecimal(3, execution.getAmount());
                ps.setString(4, execution.getCode());
                ps.setBigDecimal(5, execution.getPrice());
                ps.setBigDecimal(6, execution.getTotalPrice());
                ps.setTimestamp(7, Timestamp.valueOf(execution.getDateTime()));
                ps.setLong(8, execution.getSequentialId());
            }

            @Override
            public int getBatchSize() {
                return executions.size();
            }
        });
    }
}
