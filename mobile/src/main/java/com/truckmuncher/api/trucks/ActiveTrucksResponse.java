// Code generated by Wire protocol buffer compiler, do not edit.
// Source file: /Volumes/home/projects/TruckMuncher-Protos/com/truckmuncher/api/trucks.proto
package com.truckmuncher.api.trucks;

import com.squareup.wire.Message;
import com.squareup.wire.ProtoField;
import java.util.Collections;
import java.util.List;

import static com.squareup.wire.Message.Datatype.DOUBLE;
import static com.squareup.wire.Message.Datatype.STRING;
import static com.squareup.wire.Message.Label.REPEATED;
import static com.squareup.wire.Message.Label.REQUIRED;

public final class ActiveTrucksResponse extends Message {

  public static final List<Truck> DEFAULT_TRUCKS = Collections.emptyList();

  @ProtoField(tag = 1, label = REPEATED)
  public final List<Truck> trucks;

  public ActiveTrucksResponse(List<Truck> trucks) {
    this.trucks = immutableCopyOf(trucks);
  }

  private ActiveTrucksResponse(Builder builder) {
    this(builder.trucks);
    setBuilder(builder);
  }

  @Override
  public boolean equals(Object other) {
    if (other == this) return true;
    if (!(other instanceof ActiveTrucksResponse)) return false;
    return equals(trucks, ((ActiveTrucksResponse) other).trucks);
  }

  @Override
  public int hashCode() {
    int result = hashCode;
    return result != 0 ? result : (hashCode = trucks != null ? trucks.hashCode() : 1);
  }

  public static final class Builder extends Message.Builder<ActiveTrucksResponse> {

    public List<Truck> trucks;

    public Builder() {
    }

    public Builder(ActiveTrucksResponse message) {
      super(message);
      if (message == null) return;
      this.trucks = copyOf(message.trucks);
    }

    public Builder trucks(List<Truck> trucks) {
      this.trucks = checkForNulls(trucks);
      return this;
    }

    @Override
    public ActiveTrucksResponse build() {
      return new ActiveTrucksResponse(this);
    }
  }

  public static final class Truck extends Message {

    public static final String DEFAULT_ID = "";
    public static final Double DEFAULT_LATITUDE = 0D;
    public static final Double DEFAULT_LONGITUDE = 0D;

    /**
     * Suitable for unique identification.
     */
    @ProtoField(tag = 1, type = STRING, label = REQUIRED)
    public final String id;

    /**
     * Value in the range [-90, 90]
     */
    @ProtoField(tag = 2, type = DOUBLE, label = REQUIRED)
    public final Double latitude;

    /**
     * Value in the range [-180, 180]
     */
    @ProtoField(tag = 3, type = DOUBLE, label = REQUIRED)
    public final Double longitude;

    public Truck(String id, Double latitude, Double longitude) {
      this.id = id;
      this.latitude = latitude;
      this.longitude = longitude;
    }

    private Truck(Builder builder) {
      this(builder.id, builder.latitude, builder.longitude);
      setBuilder(builder);
    }

    @Override
    public boolean equals(Object other) {
      if (other == this) return true;
      if (!(other instanceof Truck)) return false;
      Truck o = (Truck) other;
      return equals(id, o.id)
          && equals(latitude, o.latitude)
          && equals(longitude, o.longitude);
    }

    @Override
    public int hashCode() {
      int result = hashCode;
      if (result == 0) {
        result = id != null ? id.hashCode() : 0;
        result = result * 37 + (latitude != null ? latitude.hashCode() : 0);
        result = result * 37 + (longitude != null ? longitude.hashCode() : 0);
        hashCode = result;
      }
      return result;
    }

    public static final class Builder extends Message.Builder<Truck> {

      public String id;
      public Double latitude;
      public Double longitude;

      public Builder() {
      }

      public Builder(Truck message) {
        super(message);
        if (message == null) return;
        this.id = message.id;
        this.latitude = message.latitude;
        this.longitude = message.longitude;
      }

      /**
       * Suitable for unique identification.
       */
      public Builder id(String id) {
        this.id = id;
        return this;
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
      public Truck build() {
        checkRequiredFields();
        return new Truck(this);
      }
    }
  }
}
