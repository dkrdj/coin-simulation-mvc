package com.mvc.coinsimulation.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.mvc.coinsimulation.enums.Gubun;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ExecutionResponse {
    private Long id;
    private Gubun gubun;
    private BigDecimal amount;
    private String code;
    private BigDecimal price;
    private BigDecimal totalPrice;
    @JsonFormat(pattern = "yyyy-MM-dd kk:mm:ss")
    private LocalDateTime dateTime;
}
