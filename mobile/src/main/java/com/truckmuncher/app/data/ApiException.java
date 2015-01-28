package com.truckmuncher.app.data;

public class ApiException extends RuntimeException {

    public ApiException(String message, Throwable cause) {
        super(message, cause);
    }
}
