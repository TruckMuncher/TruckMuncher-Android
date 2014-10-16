package com.truckmuncher.truckmuncher.data;

public class ApiException extends RuntimeException {

    public ApiException(String message, Throwable cause) {
        super(message, cause);
    }
}
