// Code generated by Wire protocol buffer compiler, do not edit.
// Source file: /Volumes/home/projects/TruckMuncher-Protos/com/truckmuncher/api/menu.proto
package com.truckmuncher.api.menu;

import com.squareup.wire.Message;
import com.squareup.wire.ProtoField;
import java.util.Collections;
import java.util.List;

import static com.squareup.wire.Message.Datatype.STRING;
import static com.squareup.wire.Message.Label.REPEATED;
import static com.squareup.wire.Message.Label.REQUIRED;

/**
 * Menu's are meta-objects used only for data transport
 */
public final class Menu extends Message {

  public static final String DEFAULT_TRUCKID = "";
  public static final List<Category> DEFAULT_CATEGORIES = Collections.emptyList();

  /**
   * Suitable for unique identification.
   */
  @ProtoField(tag = 1, type = STRING, label = REQUIRED)
  public final String truckId;

  @ProtoField(tag = 2, label = REPEATED)
  public final List<Category> categories;

  public Menu(String truckId, List<Category> categories) {
    this.truckId = truckId;
    this.categories = immutableCopyOf(categories);
  }

  private Menu(Builder builder) {
    this(builder.truckId, builder.categories);
    setBuilder(builder);
  }

  @Override
  public boolean equals(Object other) {
    if (other == this) return true;
    if (!(other instanceof Menu)) return false;
    Menu o = (Menu) other;
    return equals(truckId, o.truckId)
        && equals(categories, o.categories);
  }

  @Override
  public int hashCode() {
    int result = hashCode;
    if (result == 0) {
      result = truckId != null ? truckId.hashCode() : 0;
      result = result * 37 + (categories != null ? categories.hashCode() : 1);
      hashCode = result;
    }
    return result;
  }

  public static final class Builder extends Message.Builder<Menu> {

    public String truckId;
    public List<Category> categories;

    public Builder() {
    }

    public Builder(Menu message) {
      super(message);
      if (message == null) return;
      this.truckId = message.truckId;
      this.categories = copyOf(message.categories);
    }

    /**
     * Suitable for unique identification.
     */
    public Builder truckId(String truckId) {
      this.truckId = truckId;
      return this;
    }

    public Builder categories(List<Category> categories) {
      this.categories = checkForNulls(categories);
      return this;
    }

    @Override
    public Menu build() {
      checkRequiredFields();
      return new Menu(this);
    }
  }
}
