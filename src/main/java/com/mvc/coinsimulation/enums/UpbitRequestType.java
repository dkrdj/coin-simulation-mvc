package com.mvc.coinsimulation.enums;

public enum UpbitRequestType {
    ORDERBOOK("orderbook"),
    TICKER("ticker"),
    TRADE("trade");


    private final String value;

    UpbitRequestType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
