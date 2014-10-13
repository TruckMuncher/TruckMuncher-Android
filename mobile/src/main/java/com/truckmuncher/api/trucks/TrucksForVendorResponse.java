// Code generated by Wire protocol buffer compiler, do not edit.
// Source file: /Volumes/home/Dropbox/workspace/TruckMuncher-Protos/com/truckmuncher/api/trucks.proto
package com.truckmuncher.api.trucks;

import com.squareup.wire.Message;
import com.squareup.wire.ProtoField;
import java.util.Collections;
import java.util.List;

import static com.squareup.wire.Message.Datatype.BOOL;
import static com.squareup.wire.Message.Label.REPEATED;
import static com.squareup.wire.Message.Label.REQUIRED;

/**
 * The included trucks will always have a valid id.
 */
public final class TrucksForVendorResponse extends Message {

  public static final List<Truck> DEFAULT_TRUCKS = Collections.emptyList();
  public static final Boolean DEFAULT_ISNEW = true;

  @ProtoField(tag = 1, label = REPEATED)
  public final List<Truck> trucks;

  /**
   * If true, then a new Truck was created during this request. On native apps, this will need to be handled.
   * If false, the trucks previously existed.
   */
  @ProtoField(tag = 2, type = BOOL, label = REQUIRED)
  public final Boolean isNew;

  public TrucksForVendorResponse(List<Truck> trucks, Boolean isNew) {
    this.trucks = immutableCopyOf(trucks);
    this.isNew = isNew;
  }

  private TrucksForVendorResponse(Builder builder) {
    this(builder.trucks, builder.isNew);
    setBuilder(builder);
  }

  @Override
  public boolean equals(Object other) {
    if (other == this) return true;
    if (!(other instanceof TrucksForVendorResponse)) return false;
    TrucksForVendorResponse o = (TrucksForVendorResponse) other;
    return equals(trucks, o.trucks)
        && equals(isNew, o.isNew);
  }

  @Override
  public int hashCode() {
    int result = hashCode;
    if (result == 0) {
      result = trucks != null ? trucks.hashCode() : 1;
      result = result * 37 + (isNew != null ? isNew.hashCode() : 0);
      hashCode = result;
    }
    return result;
  }

  public static final class Builder extends Message.Builder<TrucksForVendorResponse> {

    public List<Truck> trucks;
    public Boolean isNew;

    public Builder() {
    }

    public Builder(TrucksForVendorResponse message) {
      super(message);
      if (message == null) return;
      this.trucks = copyOf(message.trucks);
      this.isNew = message.isNew;
    }

    public Builder trucks(List<Truck> trucks) {
      this.trucks = checkForNulls(trucks);
      return this;
    }

    /**
     * If true, then a new Truck was created during this request. On native apps, this will need to be handled.
     * If false, the trucks previously existed.
     */
    public Builder isNew(Boolean isNew) {
      this.isNew = isNew;
      return this;
    }

    @Override
    public TrucksForVendorResponse build() {
      checkRequiredFields();
      return new TrucksForVendorResponse(this);
    }
  }
}
