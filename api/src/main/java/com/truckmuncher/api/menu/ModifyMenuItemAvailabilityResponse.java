// Code generated by Wire protocol buffer compiler, do not edit.
// Source file: /Volumes/home/projects/TruckMuncher-Protos/com/truckmuncher/api/menu.proto
package com.truckmuncher.api.menu;

import com.squareup.wire.Message;

public final class ModifyMenuItemAvailabilityResponse extends Message {

    public ModifyMenuItemAvailabilityResponse() {
    }

    private ModifyMenuItemAvailabilityResponse(Builder builder) {
        setBuilder(builder);
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof ModifyMenuItemAvailabilityResponse;
    }

    @Override
    public int hashCode() {
        return 0;
    }

    public static final class Builder extends Message.Builder<ModifyMenuItemAvailabilityResponse> {

        public Builder() {
        }

        public Builder(ModifyMenuItemAvailabilityResponse message) {
            super(message);
        }

        @Override
        public ModifyMenuItemAvailabilityResponse build() {
            return new ModifyMenuItemAvailabilityResponse(this);
        }
    }
}
