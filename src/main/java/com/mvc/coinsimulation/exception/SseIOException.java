package com.mvc.coinsimulation.exception;

public class SseIOException extends RuntimeException {
    public SseIOException() {
        super("SSE I/O 예외 발생");
    }
}
