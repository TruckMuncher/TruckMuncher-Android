package com.truckmuncher.app.data;

public class ExpiredSessionException extends ApiException {

    public ExpiredSessionException(String message, Throwable cause) {
        super(message, cause);
    }
}
