package com.mvc.coinsimulation.exception;

public class CashOverException extends RuntimeException {
    public CashOverException() {
        super("현재 자선(코인+현금)이 천만 원 이상 존재합니다.");
    }
}
