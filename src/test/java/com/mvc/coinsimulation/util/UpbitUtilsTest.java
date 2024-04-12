package com.mvc.coinsimulation.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.mvc.coinsimulation.enums.UpbitRequestType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UpbitUtilsTest {

    @Test
    void makeBody() throws JsonProcessingException {
        String ticker = UpbitUtils.makeBody(UpbitRequestType.TICKER);
        assertEquals("[{\"ticket\":\"test example\"},{\"type\":\"TICKER\",\"codes\":[\"KRW-BTC\"],\"isOnlySnapshot\":false,\"isOnlyRealtime\":true},{\"format\":\"DEFAULT\"}]", ticker);

        String trade = UpbitUtils.makeBody(UpbitRequestType.TRADE);
        assertEquals("[{\"ticket\":\"test example\"},{\"type\":\"TRADE\",\"codes\":[\"KRW-BTC\"],\"isOnlySnapshot\":false,\"isOnlyRealtime\":true},{\"format\":\"DEFAULT\"}]", trade);

        String orderbook = UpbitUtils.makeBody(UpbitRequestType.ORDERBOOK);
        assertEquals("[{\"ticket\":\"test example\"},{\"type\":\"ORDERBOOK\",\"codes\":[\"KRW-BTC\"],\"isOnlySnapshot\":false,\"isOnlyRealtime\":true},{\"format\":\"DEFAULT\"}]", orderbook);
    }
}