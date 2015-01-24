// Code generated by Wire protocol buffer compiler, do not edit.
// Source file: /Users/brettnockerts/Documents/TruckMuncher/TruckMuncher-Protos/com/truckmuncher/api/search.proto
package com.truckmuncher.api.search;

import com.squareup.wire.Message;
import com.squareup.wire.ProtoField;

import static com.squareup.wire.Message.Datatype.INT32;
import static com.squareup.wire.Message.Datatype.STRING;
import static com.squareup.wire.Message.Label.REQUIRED;

public final class SimpleSearchRequest extends Message {

    public static final String DEFAULT_QUERY = "";
    public static final Integer DEFAULT_LIMIT = 20;
    public static final Integer DEFAULT_OFFSET = 0;

    /**
     * A string containing any number of terms to search for
     */
    @ProtoField(tag = 1, type = STRING, label = REQUIRED)
    public final String query;

    /**
     * The number of results to return
     */
    @ProtoField(tag = 2, type = INT32)
    public final Integer limit;

    /**
     * The number of results to skip
     */
    @ProtoField(tag = 3, type = INT32)
    public final Integer offset;

    public SimpleSearchRequest(String query, Integer limit, Integer offset) {
        this.query = query;
        this.limit = limit;
        this.offset = offset;
    }

    private SimpleSearchRequest(Builder builder) {
        this(builder.query, builder.limit, builder.offset);
        setBuilder(builder);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) return true;
        if (!(other instanceof SimpleSearchRequest)) return false;
        SimpleSearchRequest o = (SimpleSearchRequest) other;
        return equals(query, o.query)
                && equals(limit, o.limit)
                && equals(offset, o.offset);
    }

    @Override
    public int hashCode() {
        int result = hashCode;
        if (result == 0) {
            result = query != null ? query.hashCode() : 0;
            result = result * 37 + (limit != null ? limit.hashCode() : 0);
            result = result * 37 + (offset != null ? offset.hashCode() : 0);
            hashCode = result;
        }
        return result;
    }

    public static final class Builder extends Message.Builder<SimpleSearchRequest> {

        public String query;
        public Integer limit;
        public Integer offset;

        public Builder() {
        }

        public Builder(SimpleSearchRequest message) {
            super(message);
            if (message == null) return;
            this.query = message.query;
            this.limit = message.limit;
            this.offset = message.offset;
        }

        /**
         * A string containing any number of terms to search for
         */
        public Builder query(String query) {
            this.query = query;
            return this;
        }

        /**
         * The number of results to return
         */
        public Builder limit(Integer limit) {
            this.limit = limit;
            return this;
        }

        /**
         * The number of results to skip
         */
        public Builder offset(Integer offset) {
            this.offset = offset;
            return this;
        }

        @Override
        public SimpleSearchRequest build() {
            checkRequiredFields();
            return new SimpleSearchRequest(this);
        }
    }
}
