package com.truckmuncher.truckmuncher.data;

public class ExpiredSessionException extends ApiException {

    public ExpiredSessionException(String message, Throwable cause) {
        super(message, cause);
    }
}
