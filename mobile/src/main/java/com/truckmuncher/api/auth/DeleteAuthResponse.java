// Code generated by Wire protocol buffer compiler, do not edit.
// Source file: /Volumes/home/projects/TruckMuncher-Protos/com/truckmuncher/api/auth.proto
package com.truckmuncher.api.auth;

import com.squareup.wire.Message;

public final class DeleteAuthResponse extends Message {

  public DeleteAuthResponse() {
  }

  private DeleteAuthResponse(Builder builder) {
    setBuilder(builder);
  }

  @Override
  public boolean equals(Object other) {
    return other instanceof DeleteAuthResponse;
  }

  @Override
  public int hashCode() {
    return 0;
  }

  public static final class Builder extends Message.Builder<DeleteAuthResponse> {

    public Builder() {
    }

    public Builder(DeleteAuthResponse message) {
      super(message);
    }

    @Override
    public DeleteAuthResponse build() {
      return new DeleteAuthResponse(this);
    }
  }
}
