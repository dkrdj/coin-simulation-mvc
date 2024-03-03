package com.mvc.coinsimulation.entity;

import com.mvc.coinsimulation.dto.response.ExecutionResponse;
import com.mvc.coinsimulation.dto.response.ExecutionSSEResponse;
import jakarta.persistence.Entity;
import lombok.*;
import org.springframework.beans.BeanUtils;
import jakarta.persistence.Id;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@Entity
public class Execution {
    @Id
    private Long id;
    private Long userId;
    private String gubun;
    private Double amount;
    private String code;
    private Double price;
    private Double totalPrice;
    private LocalDateTime dateTime;
    private Long sequentialId;

    public ExecutionResponse toResponse() {
        ExecutionResponse executionResponse = new ExecutionResponse();
        BeanUtils.copyProperties(this, executionResponse);
        return executionResponse;
    }

    public ExecutionSSEResponse toSSEResponse() {
        ExecutionSSEResponse sseResponse = new ExecutionSSEResponse();
        BeanUtils.copyProperties(this, sseResponse);
        return sseResponse;
    }
}
