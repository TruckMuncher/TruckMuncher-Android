// Code generated by Wire protocol buffer compiler, do not edit.
// Source file: /Volumes/home/Dropbox/workspace/TruckMuncher-Protos/com/truckmuncher/api/menu.proto
package com.truckmuncher.api.menu;

import com.squareup.wire.Message;
import com.squareup.wire.ProtoField;

import static com.squareup.wire.Message.Datatype.INT64;
import static com.squareup.wire.Message.Label.REQUIRED;

public final class DeleteCategoryRequest extends Message {

  public static final Long DEFAULT_CATEGORYID = 0L;

  /**
   * Non-negative value. Suitable for unique identification.
   */
  @ProtoField(tag = 1, type = INT64, label = REQUIRED)
  public final Long categoryId;

  public DeleteCategoryRequest(Long categoryId) {
    this.categoryId = categoryId;
  }

  private DeleteCategoryRequest(Builder builder) {
    this(builder.categoryId);
    setBuilder(builder);
  }

  @Override
  public boolean equals(Object other) {
    if (other == this) return true;
    if (!(other instanceof DeleteCategoryRequest)) return false;
    return equals(categoryId, ((DeleteCategoryRequest) other).categoryId);
  }

  @Override
  public int hashCode() {
    int result = hashCode;
    return result != 0 ? result : (hashCode = categoryId != null ? categoryId.hashCode() : 0);
  }

  public static final class Builder extends Message.Builder<DeleteCategoryRequest> {

    public Long categoryId;

    public Builder() {
    }

    public Builder(DeleteCategoryRequest message) {
      super(message);
      if (message == null) return;
      this.categoryId = message.categoryId;
    }

    /**
     * Non-negative value. Suitable for unique identification.
     */
    public Builder categoryId(Long categoryId) {
      this.categoryId = categoryId;
      return this;
    }

    @Override
    public DeleteCategoryRequest build() {
      checkRequiredFields();
      return new DeleteCategoryRequest(this);
    }
  }
}
