package com.mvc.coinsimulation.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.mvc.coinsimulation.enums.Gubun;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ExecutionSseResponse {
    private Gubun gubun;
    private String code;
    private BigDecimal price;
    private BigDecimal amount;
    @JsonFormat(pattern = "yyyy-MM-dd kk:mm:ss")
    private LocalDateTime dateTime;
}
