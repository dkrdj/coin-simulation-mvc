package com.mvc.coinsimulation.dto.common;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mvc.coinsimulation.enums.Gubun;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;


@AllArgsConstructor
@NoArgsConstructor
@ToString
@Setter
@Getter
public class TicketDto {
    private String id;
    private String type;
    private String code;
    private BigDecimal openingPrice;
    private BigDecimal highPrice;
    private BigDecimal lowPrice;
    private BigDecimal tradePrice;
    private BigDecimal prevClosingPrice;
    private String change;
    private BigDecimal changePrice;
    private BigDecimal signedChangePrice;
    private BigDecimal changeRate;
    private BigDecimal signedChangeRate;
    private BigDecimal tradeVolume;
    private BigDecimal accTradeVolume;
    @JsonProperty("acc_trade_volume_24h")
    private BigDecimal accTradeVolume24h;
    private BigDecimal accTradePrice;
    @JsonProperty("acc_trade_price_24h")
    private BigDecimal accTradePrice24h;
    private String tradeDate;
    private String tradeTime;
    private Long tradeTimestamp;
    private Gubun askBid;
    private BigDecimal accAskVolume;
    private BigDecimal accBidVolume;
    @JsonProperty("highest_52_week_price")
    private BigDecimal highest52WeekPrice;
    @JsonProperty("highest_52_week_date")
    private String highest52WeekDate;
    @JsonProperty("lowest_52_week_price")
    private BigDecimal lowest52WeekPrice;
    @JsonProperty("lowest_52_week_date")
    private String lowest52WeekDate;
    private String tradeStatus;
    private String marketState;
    private String marketStateForIos;
    private Boolean isTradingSuspended;
    private LocalDateTime delistingDate;
    private String marketWarning;
    private Long timestamp;
    private String streamType;
}