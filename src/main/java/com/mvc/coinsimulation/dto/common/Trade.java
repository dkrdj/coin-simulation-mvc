package com.mvc.coinsimulation.dto.common;

import com.mvc.coinsimulation.enums.Gubun;
import lombok.Data;

@Data
public class Trade {
    private String type;
    private String code;
    private Double tradePrice;
    private Double tradeVolume;
    private Gubun askBid;
    private Double prevClosingPrice;
    private String change;
    private Double changePrice;
    private String tradeDate;
    private String tradeTime;
    private Long tradeTimestamp;
    private Long timestamp;
    private Long sequentialId;
    private String streamType;
}
