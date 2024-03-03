package com.mvc.coinsimulation.dto.common;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties({"level"})
public class OrderBook {
    private List<OrderBookUnit> orderbookUnits;
    private String type;
    private String code;
    private Long timestamp;
    private Double totalAskSize;
    private Double totalBidSize;
    private String streamType;
}
