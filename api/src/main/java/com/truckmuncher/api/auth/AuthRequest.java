// Code generated by Wire protocol buffer compiler, do not edit.
// Source file: /Volumes/home/projects/TruckMuncher-Protos/com/truckmuncher/api/auth.proto
package com.truckmuncher.api.auth;

import com.squareup.wire.Message;

public final class AuthRequest extends Message {

    public AuthRequest() {
    }

    private AuthRequest(Builder builder) {
        setBuilder(builder);
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof AuthRequest;
    }

    @Override
    public int hashCode() {
        return 0;
    }

    public static final class Builder extends Message.Builder<AuthRequest> {

        public Builder() {
        }

        public Builder(AuthRequest message) {
            super(message);
        }

        @Override
        public AuthRequest build() {
            return new AuthRequest(this);
        }
    }
}
