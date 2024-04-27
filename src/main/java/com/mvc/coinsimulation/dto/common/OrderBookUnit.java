package com.mvc.coinsimulation.dto.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderBookUnit {
    private BigDecimal askPrice;
    private BigDecimal bidPrice;
    private BigDecimal askSize;
    private BigDecimal bidSize;
}
