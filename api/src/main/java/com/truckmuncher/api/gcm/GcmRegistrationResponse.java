// Code generated by Wire protocol buffer compiler, do not edit.
// Source file: /Volumes/home/projects/TruckMuncher-Protos/com/truckmuncher/api/gcm.proto
package com.truckmuncher.api.gcm;

import com.squareup.wire.Message;

public final class GcmRegistrationResponse extends Message {

    public GcmRegistrationResponse() {
    }

    private GcmRegistrationResponse(Builder builder) {
        setBuilder(builder);
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof GcmRegistrationResponse;
    }

    @Override
    public int hashCode() {
        return 0;
    }

    public static final class Builder extends Message.Builder<GcmRegistrationResponse> {

        public Builder() {
        }

        public Builder(GcmRegistrationResponse message) {
            super(message);
        }

        @Override
        public GcmRegistrationResponse build() {
            return new GcmRegistrationResponse(this);
        }
    }
}
