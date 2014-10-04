// Code generated by Wire protocol buffer compiler, do not edit.
// Source file: /Volumes/home/Dropbox/workspace/TruckMuncher-Protos/com/truckmuncher/api/healthcheck.proto
package com.truckmuncher.api.healthcheck;

import com.squareup.wire.Message;
import com.squareup.wire.ProtoEnum;
import com.squareup.wire.ProtoField;
import java.util.Collections;
import java.util.List;

import static com.squareup.wire.Message.Datatype.ENUM;
import static com.squareup.wire.Message.Datatype.STRING;
import static com.squareup.wire.Message.Label.REPEATED;
import static com.squareup.wire.Message.Label.REQUIRED;

public final class HealthResponse extends Message {

  public static final Status DEFAULT_STATUS = Status.BAD;
  public static final String DEFAULT_REVISION = "";
  public static final Status DEFAULT_NONCE = Status.BAD;
  public static final Status DEFAULT_TIMESTAMP = Status.BAD;
  public static final List<Check> DEFAULT_CHECKS = Collections.emptyList();
  public static final Status DEFAULT_EXTERNALSERVICESSTATUS = Status.OK;
  public static final List<ExternalService> DEFAULT_EXTERNALSERVICES = Collections.emptyList();

  @ProtoField(tag = 1, type = ENUM, label = REQUIRED)
  public final Status status;

  @ProtoField(tag = 2, type = STRING)
  public final String revision;

  @ProtoField(tag = 3, type = ENUM)
  public final Status nonce;

  @ProtoField(tag = 4, type = ENUM)
  public final Status timestamp;

  @ProtoField(tag = 5, label = REPEATED)
  public final List<Check> checks;

  @ProtoField(tag = 6, type = ENUM)
  public final Status externalServicesStatus;

  @ProtoField(tag = 7, label = REPEATED)
  public final List<ExternalService> externalServices;

  public HealthResponse(Status status, String revision, Status nonce, Status timestamp, List<Check> checks, Status externalServicesStatus, List<ExternalService> externalServices) {
    this.status = status;
    this.revision = revision;
    this.nonce = nonce;
    this.timestamp = timestamp;
    this.checks = immutableCopyOf(checks);
    this.externalServicesStatus = externalServicesStatus;
    this.externalServices = immutableCopyOf(externalServices);
  }

  private HealthResponse(Builder builder) {
    this(builder.status, builder.revision, builder.nonce, builder.timestamp, builder.checks, builder.externalServicesStatus, builder.externalServices);
    setBuilder(builder);
  }

  @Override
  public boolean equals(Object other) {
    if (other == this) return true;
    if (!(other instanceof HealthResponse)) return false;
    HealthResponse o = (HealthResponse) other;
    return equals(status, o.status)
        && equals(revision, o.revision)
        && equals(nonce, o.nonce)
        && equals(timestamp, o.timestamp)
        && equals(checks, o.checks)
        && equals(externalServicesStatus, o.externalServicesStatus)
        && equals(externalServices, o.externalServices);
  }

  @Override
  public int hashCode() {
    int result = hashCode;
    if (result == 0) {
      result = status != null ? status.hashCode() : 0;
      result = result * 37 + (revision != null ? revision.hashCode() : 0);
      result = result * 37 + (nonce != null ? nonce.hashCode() : 0);
      result = result * 37 + (timestamp != null ? timestamp.hashCode() : 0);
      result = result * 37 + (checks != null ? checks.hashCode() : 1);
      result = result * 37 + (externalServicesStatus != null ? externalServicesStatus.hashCode() : 0);
      result = result * 37 + (externalServices != null ? externalServices.hashCode() : 1);
      hashCode = result;
    }
    return result;
  }

  public static final class Builder extends Message.Builder<HealthResponse> {

    public Status status;
    public String revision;
    public Status nonce;
    public Status timestamp;
    public List<Check> checks;
    public Status externalServicesStatus;
    public List<ExternalService> externalServices;

    public Builder() {
    }

    public Builder(HealthResponse message) {
      super(message);
      if (message == null) return;
      this.status = message.status;
      this.revision = message.revision;
      this.nonce = message.nonce;
      this.timestamp = message.timestamp;
      this.checks = copyOf(message.checks);
      this.externalServicesStatus = message.externalServicesStatus;
      this.externalServices = copyOf(message.externalServices);
    }

    public Builder status(Status status) {
      if (status == Status.__UNDEFINED__) throw new IllegalArgumentException();
      this.status = status;
      return this;
    }

    public Builder revision(String revision) {
      this.revision = revision;
      return this;
    }

    public Builder nonce(Status nonce) {
      if (nonce == Status.__UNDEFINED__) throw new IllegalArgumentException();
      this.nonce = nonce;
      return this;
    }

    public Builder timestamp(Status timestamp) {
      if (timestamp == Status.__UNDEFINED__) throw new IllegalArgumentException();
      this.timestamp = timestamp;
      return this;
    }

    public Builder checks(List<Check> checks) {
      this.checks = checkForNulls(checks);
      return this;
    }

    public Builder externalServicesStatus(Status externalServicesStatus) {
      if (externalServicesStatus == Status.__UNDEFINED__) throw new IllegalArgumentException();
      this.externalServicesStatus = externalServicesStatus;
      return this;
    }

    public Builder externalServices(List<ExternalService> externalServices) {
      this.externalServices = checkForNulls(externalServices);
      return this;
    }

    @Override
    public HealthResponse build() {
      checkRequiredFields();
      return new HealthResponse(this);
    }
  }

  public static final class Check extends Message {

    public static final String DEFAULT_KEY = "";
    public static final Status DEFAULT_VALUE = Status.OK;

    @ProtoField(tag = 1, type = STRING, label = REQUIRED)
    public final String key;

    @ProtoField(tag = 2, type = ENUM, label = REQUIRED)
    public final Status value;

    public Check(String key, Status value) {
      this.key = key;
      this.value = value;
    }

    private Check(Builder builder) {
      this(builder.key, builder.value);
      setBuilder(builder);
    }

    @Override
    public boolean equals(Object other) {
      if (other == this) return true;
      if (!(other instanceof Check)) return false;
      Check o = (Check) other;
      return equals(key, o.key)
          && equals(value, o.value);
    }

    @Override
    public int hashCode() {
      int result = hashCode;
      if (result == 0) {
        result = key != null ? key.hashCode() : 0;
        result = result * 37 + (value != null ? value.hashCode() : 0);
        hashCode = result;
      }
      return result;
    }

    public static final class Builder extends Message.Builder<Check> {

      public String key;
      public Status value;

      public Builder() {
      }

      public Builder(Check message) {
        super(message);
        if (message == null) return;
        this.key = message.key;
        this.value = message.value;
      }

      public Builder key(String key) {
        this.key = key;
        return this;
      }

      public Builder value(Status value) {
        if (value == Status.__UNDEFINED__) throw new IllegalArgumentException();
        this.value = value;
        return this;
      }

      @Override
      public Check build() {
        checkRequiredFields();
        return new Check(this);
      }
    }
  }

  public enum Status
      implements ProtoEnum {
    OK(1),
    BAD(2),

    /**
     * Wire-generated value, do not access from application code.
     */
    __UNDEFINED__(UNDEFINED_VALUE);

    private final int value;

    private Status(int value) {
      this.value = value;
    }

    @Override
    public int getValue() {
      return value;
    }
  }

  public static final class ExternalService extends Message {

    public static final String DEFAULT_KEY = "";
    public static final String DEFAULT_VALUE = "";

    @ProtoField(tag = 1, type = STRING, label = REQUIRED)
    public final String key;

    @ProtoField(tag = 2, type = STRING)
    public final String value;

    public ExternalService(String key, String value) {
      this.key = key;
      this.value = value;
    }

    private ExternalService(Builder builder) {
      this(builder.key, builder.value);
      setBuilder(builder);
    }

    @Override
    public boolean equals(Object other) {
      if (other == this) return true;
      if (!(other instanceof ExternalService)) return false;
      ExternalService o = (ExternalService) other;
      return equals(key, o.key)
          && equals(value, o.value);
    }

    @Override
    public int hashCode() {
      int result = hashCode;
      if (result == 0) {
        result = key != null ? key.hashCode() : 0;
        result = result * 37 + (value != null ? value.hashCode() : 0);
        hashCode = result;
      }
      return result;
    }

    public static final class Builder extends Message.Builder<ExternalService> {

      public String key;
      public String value;

      public Builder() {
      }

      public Builder(ExternalService message) {
        super(message);
        if (message == null) return;
        this.key = message.key;
        this.value = message.value;
      }

      public Builder key(String key) {
        this.key = key;
        return this;
      }

      public Builder value(String value) {
        this.value = value;
        return this;
      }

      @Override
      public ExternalService build() {
        checkRequiredFields();
        return new ExternalService(this);
      }
    }
  }
}
