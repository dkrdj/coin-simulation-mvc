package com.mvc.coinsimulation.exception;

public class NoUserException extends RuntimeException {
    public NoUserException() {
        super("해당 유저가 없습니다.");
    }
}
