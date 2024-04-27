package com.mvc.coinsimulation.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class OrderRequest {
    private String code;
    private BigDecimal price;
    private BigDecimal amount;
}
