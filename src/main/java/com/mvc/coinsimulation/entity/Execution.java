package com.mvc.coinsimulation.entity;

import com.mvc.coinsimulation.dto.response.ExecutionResponse;
import com.mvc.coinsimulation.dto.response.ExecutionSseResponse;
import com.mvc.coinsimulation.enums.Gubun;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.beans.BeanUtils;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@ToString
@Entity
public class Execution {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long userId;
    @Enumerated(EnumType.STRING)
    private Gubun gubun;
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

    public ExecutionSseResponse toSseResponse() {
        ExecutionSseResponse sseResponse = new ExecutionSseResponse();
        BeanUtils.copyProperties(this, sseResponse);
        return sseResponse;
    }
}
