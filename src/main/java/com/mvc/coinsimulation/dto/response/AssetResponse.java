package com.mvc.coinsimulation.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Builder
@Data
public class AssetResponse {
    private String code;
    private BigDecimal amount;
    private BigDecimal buyingPrice;
    private BigDecimal currentPrice;
}
