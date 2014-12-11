// Code generated by Wire protocol buffer compiler, do not edit.
// Source file: /Volumes/home/projects/TruckMuncher-Protos/com/truckmuncher/api/menu.proto
package com.truckmuncher.api.menu;

import com.squareup.wire.Message;
import com.squareup.wire.ProtoField;
import java.util.Collections;
import java.util.List;

import static com.squareup.wire.Message.Label.REPEATED;

public final class ModifyMenuItemAvailabilityRequest extends Message {

  public static final List<MenuItemAvailability> DEFAULT_DIFF = Collections.emptyList();

  /**
   * The changes that should be applied on the server.
   */
  @ProtoField(tag = 1, label = REPEATED)
  public final List<MenuItemAvailability> diff;

  public ModifyMenuItemAvailabilityRequest(List<MenuItemAvailability> diff) {
    this.diff = immutableCopyOf(diff);
  }

  private ModifyMenuItemAvailabilityRequest(Builder builder) {
    this(builder.diff);
    setBuilder(builder);
  }

  @Override
  public boolean equals(Object other) {
    if (other == this) return true;
    if (!(other instanceof ModifyMenuItemAvailabilityRequest)) return false;
    return equals(diff, ((ModifyMenuItemAvailabilityRequest) other).diff);
  }

  @Override
  public int hashCode() {
    int result = hashCode;
    return result != 0 ? result : (hashCode = diff != null ? diff.hashCode() : 1);
  }

  public static final class Builder extends Message.Builder<ModifyMenuItemAvailabilityRequest> {

    public List<MenuItemAvailability> diff;

    public Builder() {
    }

    public Builder(ModifyMenuItemAvailabilityRequest message) {
      super(message);
      if (message == null) return;
      this.diff = copyOf(message.diff);
    }

    /**
     * The changes that should be applied on the server.
     */
    public Builder diff(List<MenuItemAvailability> diff) {
      this.diff = checkForNulls(diff);
      return this;
    }

    @Override
    public ModifyMenuItemAvailabilityRequest build() {
      return new ModifyMenuItemAvailabilityRequest(this);
    }
  }
}
