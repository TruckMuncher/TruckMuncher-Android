// Code generated by Wire protocol buffer compiler, do not edit.
// Source file: /Volumes/home/Dropbox/workspace/TruckMuncher-Protos/com/truckmuncher/api/trucks.proto
package com.truckmuncher.api.trucks;

import com.squareup.wire.Message;
import com.squareup.wire.ProtoField;

import static com.squareup.wire.Message.Datatype.DOUBLE;
import static com.squareup.wire.Message.Label.REQUIRED;

public final class TruckProfilesRequest extends Message {

    public static final Double DEFAULT_LATITUDE = 0D;
    public static final Double DEFAULT_LONGITUDE = 0D;

    /**
     * Value in the range [-90, 90]
     */
    @ProtoField(tag = 1, type = DOUBLE, label = REQUIRED)
    public final Double latitude;

    /**
     * Value in the range [-180, 180]
     */
    @ProtoField(tag = 2, type = DOUBLE, label = REQUIRED)
    public final Double longitude;

    public TruckProfilesRequest(Double latitude, Double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    private TruckProfilesRequest(Builder builder) {
        this(builder.latitude, builder.longitude);
        setBuilder(builder);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) return true;
        if (!(other instanceof TruckProfilesRequest)) return false;
        TruckProfilesRequest o = (TruckProfilesRequest) other;
        return equals(latitude, o.latitude)
                && equals(longitude, o.longitude);
    }

    @Override
    public int hashCode() {
        int result = hashCode;
        if (result == 0) {
            result = latitude != null ? latitude.hashCode() : 0;
            result = result * 37 + (longitude != null ? longitude.hashCode() : 0);
            hashCode = result;
        }
        return result;
    }

    public static final class Builder extends Message.Builder<TruckProfilesRequest> {

        public Double latitude;
        public Double longitude;

        public Builder() {
        }

        public Builder(TruckProfilesRequest message) {
            super(message);
            if (message == null) return;
            this.latitude = message.latitude;
            this.longitude = message.longitude;
        }

        /**
         * Value in the range [-90, 90]
         */
        public Builder latitude(Double latitude) {
            this.latitude = latitude;
            return this;
        }

        /**
         * Value in the range [-180, 180]
         */
        public Builder longitude(Double longitude) {
            this.longitude = longitude;
            return this;
        }

        @Override
        public TruckProfilesRequest build() {
            checkRequiredFields();
            return new TruckProfilesRequest(this);
        }
    }
}
