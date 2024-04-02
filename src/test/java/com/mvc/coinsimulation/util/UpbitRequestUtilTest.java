package com.mvc.coinsimulation.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.mvc.coinsimulation.enums.UpbitRequestType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UpbitRequestUtilTest {

    @Test
    void makeBody() throws JsonProcessingException {
        String ticker = UpbitRequestUtil.makeBody(UpbitRequestType.TICKER);
        assertEquals("[{\"ticket\":\"test example\"},{\"type\":\"TICKER\",\"codes\":[\"KRW-BTC\"],\"isOnlySnapshot\":false,\"isOnlyRealtime\":true},{\"format\":\"DEFAULT\"}]", ticker);

        String trade = UpbitRequestUtil.makeBody(UpbitRequestType.TRADE);
        assertEquals("[{\"ticket\":\"test example\"},{\"type\":\"TRADE\",\"codes\":[\"KRW-BTC\"],\"isOnlySnapshot\":false,\"isOnlyRealtime\":true},{\"format\":\"DEFAULT\"}]", trade);

        String orderbook = UpbitRequestUtil.makeBody(UpbitRequestType.ORDERBOOK);
        assertEquals("[{\"ticket\":\"test example\"},{\"type\":\"ORDERBOOK\",\"codes\":[\"KRW-BTC\"],\"isOnlySnapshot\":false,\"isOnlyRealtime\":true},{\"format\":\"DEFAULT\"}]", orderbook);
    }
}