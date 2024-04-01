package com.mvc.coinsimulation.enums;

public enum Gubun {
    ASK("ASK"),
    BID("BID");

    private final String value;

    Gubun(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
