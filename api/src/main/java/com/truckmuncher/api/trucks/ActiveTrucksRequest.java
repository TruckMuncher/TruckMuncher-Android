// Code generated by Wire protocol buffer compiler, do not edit.
// Source file: /Volumes/home/projects/TruckMuncher-Protos/com/truckmuncher/api/trucks.proto
package com.truckmuncher.api.trucks;

import com.squareup.wire.Message;
import com.squareup.wire.ProtoField;

import static com.squareup.wire.Message.Datatype.DOUBLE;
import static com.squareup.wire.Message.Datatype.STRING;
import static com.squareup.wire.Message.Label.REQUIRED;

public final class ActiveTrucksRequest extends Message {

    public static final Double DEFAULT_LATITUDE = 0D;
    public static final Double DEFAULT_LONGITUDE = 0D;
    public static final String DEFAULT_SEARCHQUERY = "";

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

    @ProtoField(tag = 3, type = STRING)
    public final String searchQuery;

    public ActiveTrucksRequest(Double latitude, Double longitude, String searchQuery) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.searchQuery = searchQuery;
    }

    private ActiveTrucksRequest(Builder builder) {
        this(builder.latitude, builder.longitude, builder.searchQuery);
        setBuilder(builder);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) return true;
        if (!(other instanceof ActiveTrucksRequest)) return false;
        ActiveTrucksRequest o = (ActiveTrucksRequest) other;
        return equals(latitude, o.latitude)
                && equals(longitude, o.longitude)
                && equals(searchQuery, o.searchQuery);
    }

    @Override
    public int hashCode() {
        int result = hashCode;
        if (result == 0) {
            result = latitude != null ? latitude.hashCode() : 0;
            result = result * 37 + (longitude != null ? longitude.hashCode() : 0);
            result = result * 37 + (searchQuery != null ? searchQuery.hashCode() : 0);
            hashCode = result;
        }
        return result;
    }

    public static final class Builder extends Message.Builder<ActiveTrucksRequest> {

        public Double latitude;
        public Double longitude;
        public String searchQuery;

        public Builder() {
        }

        public Builder(ActiveTrucksRequest message) {
            super(message);
            if (message == null) return;
            this.latitude = message.latitude;
            this.longitude = message.longitude;
            this.searchQuery = message.searchQuery;
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

        public Builder searchQuery(String searchQuery) {
            this.searchQuery = searchQuery;
            return this;
        }

        @Override
        public ActiveTrucksRequest build() {
            checkRequiredFields();
            return new ActiveTrucksRequest(this);
        }
    }
}
