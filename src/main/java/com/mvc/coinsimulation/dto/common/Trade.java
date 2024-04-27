package com.mvc.coinsimulation.dto.common;

import com.mvc.coinsimulation.enums.Gubun;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class Trade {
    private String type;
    private String code;
    private BigDecimal tradePrice;
    private BigDecimal tradeVolume;
    private Gubun askBid;
    private BigDecimal prevClosingPrice;
    private String change;
    private BigDecimal changePrice;
    private String tradeDate;
    private String tradeTime;
    private Long tradeTimestamp;
    private Long timestamp;
    private Long sequentialId;
    private String streamType;
}
