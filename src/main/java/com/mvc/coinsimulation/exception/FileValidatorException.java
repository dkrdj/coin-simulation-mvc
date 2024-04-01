package com.mvc.coinsimulation.exception;

public class FileValidatorException extends RuntimeException {

    public FileValidatorException() {
        super("파일에 문제가 있습니다.");
    }
}