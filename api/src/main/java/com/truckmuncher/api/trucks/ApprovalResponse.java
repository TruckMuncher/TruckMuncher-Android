// Code generated by Wire protocol buffer compiler, do not edit.
// Source file: /Volumes/home/projects/TruckMuncher-Protos/com/truckmuncher/api/trucks.proto
package com.truckmuncher.api.trucks;

import com.squareup.wire.Message;

public final class ApprovalResponse extends Message {

  public ApprovalResponse() {
  }

  private ApprovalResponse(Builder builder) {
    setBuilder(builder);
  }

  @Override
  public boolean equals(Object other) {
    return other instanceof ApprovalResponse;
  }

  @Override
  public int hashCode() {
    return 0;
  }

  public static final class Builder extends Message.Builder<ApprovalResponse> {

    public Builder() {
    }

    public Builder(ApprovalResponse message) {
      super(message);
    }

    @Override
    public ApprovalResponse build() {
      return new ApprovalResponse(this);
    }
  }
}
