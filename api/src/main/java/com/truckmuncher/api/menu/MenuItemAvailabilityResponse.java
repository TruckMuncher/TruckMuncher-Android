// Code generated by Wire protocol buffer compiler, do not edit.
// Source file: /Volumes/home/projects/TruckMuncher-Protos/com/truckmuncher/api/menu.proto
package com.truckmuncher.api.menu;

import com.squareup.wire.Message;
import com.squareup.wire.ProtoField;
import java.util.Collections;
import java.util.List;

import static com.squareup.wire.Message.Label.REPEATED;

public final class MenuItemAvailabilityResponse extends Message {

  public static final List<MenuItemAvailability> DEFAULT_AVAILABILITIES = Collections.emptyList();

  /**
   * The complete MenuItem availability for the region the user is in.
   */
  @ProtoField(tag = 1, label = REPEATED)
  public final List<MenuItemAvailability> availabilities;

  public MenuItemAvailabilityResponse(List<MenuItemAvailability> availabilities) {
    this.availabilities = immutableCopyOf(availabilities);
  }

  private MenuItemAvailabilityResponse(Builder builder) {
    this(builder.availabilities);
    setBuilder(builder);
  }

  @Override
  public boolean equals(Object other) {
    if (other == this) return true;
    if (!(other instanceof MenuItemAvailabilityResponse)) return false;
    return equals(availabilities, ((MenuItemAvailabilityResponse) other).availabilities);
  }

  @Override
  public int hashCode() {
    int result = hashCode;
    return result != 0 ? result : (hashCode = availabilities != null ? availabilities.hashCode() : 1);
  }

  public static final class Builder extends Message.Builder<MenuItemAvailabilityResponse> {

    public List<MenuItemAvailability> availabilities;

    public Builder() {
    }

    public Builder(MenuItemAvailabilityResponse message) {
      super(message);
      if (message == null) return;
      this.availabilities = copyOf(message.availabilities);
    }

    /**
     * The complete MenuItem availability for the region the user is in.
     */
    public Builder availabilities(List<MenuItemAvailability> availabilities) {
      this.availabilities = checkForNulls(availabilities);
      return this;
    }

    @Override
    public MenuItemAvailabilityResponse build() {
      return new MenuItemAvailabilityResponse(this);
    }
  }
}
