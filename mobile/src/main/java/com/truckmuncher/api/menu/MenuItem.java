// Code generated by Wire protocol buffer compiler, do not edit.
// Source file: /Volumes/home/Dropbox/workspace/TruckMuncher-Protos/com/truckmuncher/api/menu.proto
package com.truckmuncher.api.menu;

import com.squareup.wire.Message;
import com.squareup.wire.ProtoField;

import java.util.Collections;
import java.util.List;

import static com.squareup.wire.Message.Datatype.BOOL;
import static com.squareup.wire.Message.Datatype.FLOAT;
import static com.squareup.wire.Message.Datatype.INT32;
import static com.squareup.wire.Message.Datatype.INT64;
import static com.squareup.wire.Message.Datatype.STRING;
import static com.squareup.wire.Message.Label.REPEATED;
import static com.squareup.wire.Message.Label.REQUIRED;

public final class MenuItem extends Message {

    public static final Long DEFAULT_ID = -1L;
    public static final String DEFAULT_NAME = "";
    public static final Float DEFAULT_PRICE = 0F;
    public static final String DEFAULT_NOTES = "";
    public static final List<String> DEFAULT_TAG = Collections.emptyList();
    public static final Integer DEFAULT_ORDERINCATEGORY = 0;
    public static final Boolean DEFAULT_ISAVAILABLE = false;

    /**
     * Suitable for unique identification. Use the default if unset.
     */
    @ProtoField(tag = 1, type = INT64, label = REQUIRED)
    public final Long id;

    @ProtoField(tag = 2, type = STRING, label = REQUIRED)
    public final String name;

    /**
     * Formatted for standard USD. Suitable for human consumption.
     */
    @ProtoField(tag = 3, type = FLOAT, label = REQUIRED)
    public final Float price;

    @ProtoField(tag = 4, type = STRING)
    public final String notes;

    /**
     * Meta data about the menu item. Ex. [bread, chicken, bacon]
     */
    @ProtoField(tag = 5, type = STRING, label = REPEATED)
    public final List<String> tag;

    @ProtoField(tag = 6, type = INT32)
    public final Integer orderInCategory;

    @ProtoField(tag = 7, type = BOOL)
    public final Boolean isAvailable;

    public MenuItem(Long id, String name, Float price, String notes, List<String> tag, Integer orderInCategory, Boolean isAvailable) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.notes = notes;
        this.tag = immutableCopyOf(tag);
        this.orderInCategory = orderInCategory;
        this.isAvailable = isAvailable;
    }

    private MenuItem(Builder builder) {
        this(builder.id, builder.name, builder.price, builder.notes, builder.tag, builder.orderInCategory, builder.isAvailable);
        setBuilder(builder);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) return true;
        if (!(other instanceof MenuItem)) return false;
        MenuItem o = (MenuItem) other;
        return equals(id, o.id)
                && equals(name, o.name)
                && equals(price, o.price)
                && equals(notes, o.notes)
                && equals(tag, o.tag)
                && equals(orderInCategory, o.orderInCategory)
                && equals(isAvailable, o.isAvailable);
    }

    @Override
    public int hashCode() {
        int result = hashCode;
        if (result == 0) {
            result = id != null ? id.hashCode() : 0;
            result = result * 37 + (name != null ? name.hashCode() : 0);
            result = result * 37 + (price != null ? price.hashCode() : 0);
            result = result * 37 + (notes != null ? notes.hashCode() : 0);
            result = result * 37 + (tag != null ? tag.hashCode() : 1);
            result = result * 37 + (orderInCategory != null ? orderInCategory.hashCode() : 0);
            result = result * 37 + (isAvailable != null ? isAvailable.hashCode() : 0);
            hashCode = result;
        }
        return result;
    }

    public static final class Builder extends Message.Builder<MenuItem> {

        public Long id;
        public String name;
        public Float price;
        public String notes;
        public List<String> tag;
        public Integer orderInCategory;
        public Boolean isAvailable;

        public Builder() {
        }

        public Builder(MenuItem message) {
            super(message);
            if (message == null) return;
            this.id = message.id;
            this.name = message.name;
            this.price = message.price;
            this.notes = message.notes;
            this.tag = copyOf(message.tag);
            this.orderInCategory = message.orderInCategory;
            this.isAvailable = message.isAvailable;
        }

        /**
         * Suitable for unique identification. Use the default if unset.
         */
        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        /**
         * Formatted for standard USD. Suitable for human consumption.
         */
        public Builder price(Float price) {
            this.price = price;
            return this;
        }

        public Builder notes(String notes) {
            this.notes = notes;
            return this;
        }

        /**
         * Meta data about the menu item. Ex. [bread, chicken, bacon]
         */
        public Builder tag(List<String> tag) {
            this.tag = checkForNulls(tag);
            return this;
        }

        public Builder orderInCategory(Integer orderInCategory) {
            this.orderInCategory = orderInCategory;
            return this;
        }

        public Builder isAvailable(Boolean isAvailable) {
            this.isAvailable = isAvailable;
            return this;
        }

        @Override
        public MenuItem build() {
            checkRequiredFields();
            return new MenuItem(this);
        }
    }
}
