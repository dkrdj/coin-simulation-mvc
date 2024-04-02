package com.mvc.coinsimulation.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.mvc.coinsimulation.enums.Gubun;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ExecutionResponse {
    private Long id;
    private Gubun gubun;
    private Double amount;
    private Double price;
    private Double totalPrice;
    @JsonFormat(pattern = "yyyy-MM-dd kk:mm:ss")
    private LocalDateTime dateTime;
}
