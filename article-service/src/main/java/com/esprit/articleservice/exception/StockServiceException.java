package com.esprit.articleservice.exception;

public class StockServiceException extends RuntimeException {
    private final String errorCode;

    public StockServiceException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }
}