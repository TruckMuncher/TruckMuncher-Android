// Code generated by Wire protocol buffer compiler, do not edit.
// Source file: /Volumes/home/Dropbox/workspace/TruckMuncher-Protos/com/truckmuncher/api/menu.proto
package com.truckmuncher.api.menu;

import com.squareup.wire.Message;
import com.squareup.wire.ProtoField;

import static com.squareup.wire.Message.Datatype.INT64;
import static com.squareup.wire.Message.Label.REQUIRED;

public final class MenuItemRequest extends Message {

    public static final Long DEFAULT_CATEGORYID = 0L;

    /**
     * Non-negative value. Suitable for unique identification.
     */
    @ProtoField(tag = 1, type = INT64, label = REQUIRED)
    public final Long categoryId;

    @ProtoField(tag = 2, label = REQUIRED)
    public final MenuItem menuItem;

    public MenuItemRequest(Long categoryId, MenuItem menuItem) {
        this.categoryId = categoryId;
        this.menuItem = menuItem;
    }

    private MenuItemRequest(Builder builder) {
        this(builder.categoryId, builder.menuItem);
        setBuilder(builder);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) return true;
        if (!(other instanceof MenuItemRequest)) return false;
        MenuItemRequest o = (MenuItemRequest) other;
        return equals(categoryId, o.categoryId)
                && equals(menuItem, o.menuItem);
    }

    @Override
    public int hashCode() {
        int result = hashCode;
        if (result == 0) {
            result = categoryId != null ? categoryId.hashCode() : 0;
            result = result * 37 + (menuItem != null ? menuItem.hashCode() : 0);
            hashCode = result;
        }
        return result;
    }

    public static final class Builder extends Message.Builder<MenuItemRequest> {

        public Long categoryId;
        public MenuItem menuItem;

        public Builder() {
        }

        public Builder(MenuItemRequest message) {
            super(message);
            if (message == null) return;
            this.categoryId = message.categoryId;
            this.menuItem = message.menuItem;
        }

        /**
         * Non-negative value. Suitable for unique identification.
         */
        public Builder categoryId(Long categoryId) {
            this.categoryId = categoryId;
            return this;
        }

        public Builder menuItem(MenuItem menuItem) {
            this.menuItem = menuItem;
            return this;
        }

        @Override
        public MenuItemRequest build() {
            checkRequiredFields();
            return new MenuItemRequest(this);
        }
    }
}
