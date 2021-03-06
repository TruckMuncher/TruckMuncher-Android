// Code generated by Wire protocol buffer compiler, do not edit.
// Source file: /Volumes/home/projects/TruckMuncher-Protos/com/truckmuncher/api/search.proto
package com.truckmuncher.api.search;

import com.squareup.wire.Message;
import com.squareup.wire.ProtoField;

import static com.squareup.wire.Message.Datatype.BOOL;
import static com.squareup.wire.Message.Datatype.INT32;
import static com.squareup.wire.Message.Datatype.STRING;
import static com.squareup.wire.Message.Label.REQUIRED;

public final class SimpleSearchRequest extends Message {

  public static final String DEFAULT_QUERY = "";
  public static final Integer DEFAULT_LIMIT = 20;
  public static final Integer DEFAULT_OFFSET = 0;
  public static final Boolean DEFAULT_SKIPCORRECTION = false;
  public static final Boolean DEFAULT_GLUTENFREE = false;
  public static final Boolean DEFAULT_PEANUTFREE = false;

  /**
   * A string containing any number of terms to search for
   */
  @ProtoField(tag = 1, type = STRING, label = REQUIRED)
  public final String query;

  /**
   * The number of results to return
   */
  @ProtoField(tag = 2, type = INT32)
  public final Integer limit;

  /**
   * The number of results to skip
   */
  @ProtoField(tag = 3, type = INT32)
  public final Integer offset;

  /**
   * A flag to signal to the API to NOT make spelling corrections and to search for the given query no matter
   * how many results are returned. First set this to false, then return the corrected results but offer the user
   * the ability to say "no I actually meant to search for XXX, don't correct it this time". Then set this flag
   * to true.
   */
  @ProtoField(tag = 4, type = BOOL)
  public final Boolean skipCorrection;

  /**
   * Flag indicating if you want to restrict your search to gluten free items only
   */
  @ProtoField(tag = 5, type = BOOL)
  public final Boolean glutenFree;

  /**
   * Flag indicating if you want to restrict you search to items without peanuts only
   */
  @ProtoField(tag = 6, type = BOOL)
  public final Boolean peanutFree;

  public SimpleSearchRequest(String query, Integer limit, Integer offset, Boolean skipCorrection, Boolean glutenFree, Boolean peanutFree) {
    this.query = query;
    this.limit = limit;
    this.offset = offset;
    this.skipCorrection = skipCorrection;
    this.glutenFree = glutenFree;
    this.peanutFree = peanutFree;
  }

  private SimpleSearchRequest(Builder builder) {
    this(builder.query, builder.limit, builder.offset, builder.skipCorrection, builder.glutenFree, builder.peanutFree);
    setBuilder(builder);
  }

  @Override
  public boolean equals(Object other) {
    if (other == this) return true;
    if (!(other instanceof SimpleSearchRequest)) return false;
    SimpleSearchRequest o = (SimpleSearchRequest) other;
    return equals(query, o.query)
        && equals(limit, o.limit)
        && equals(offset, o.offset)
        && equals(skipCorrection, o.skipCorrection)
        && equals(glutenFree, o.glutenFree)
        && equals(peanutFree, o.peanutFree);
  }

  @Override
  public int hashCode() {
    int result = hashCode;
    if (result == 0) {
      result = query != null ? query.hashCode() : 0;
      result = result * 37 + (limit != null ? limit.hashCode() : 0);
      result = result * 37 + (offset != null ? offset.hashCode() : 0);
      result = result * 37 + (skipCorrection != null ? skipCorrection.hashCode() : 0);
      result = result * 37 + (glutenFree != null ? glutenFree.hashCode() : 0);
      result = result * 37 + (peanutFree != null ? peanutFree.hashCode() : 0);
      hashCode = result;
    }
    return result;
  }

  public static final class Builder extends Message.Builder<SimpleSearchRequest> {

    public String query;
    public Integer limit;
    public Integer offset;
    public Boolean skipCorrection;
    public Boolean glutenFree;
    public Boolean peanutFree;

    public Builder() {
    }

    public Builder(SimpleSearchRequest message) {
      super(message);
      if (message == null) return;
      this.query = message.query;
      this.limit = message.limit;
      this.offset = message.offset;
      this.skipCorrection = message.skipCorrection;
      this.glutenFree = message.glutenFree;
      this.peanutFree = message.peanutFree;
    }

    /**
     * A string containing any number of terms to search for
     */
    public Builder query(String query) {
      this.query = query;
      return this;
    }

    /**
     * The number of results to return
     */
    public Builder limit(Integer limit) {
      this.limit = limit;
      return this;
    }

    /**
     * The number of results to skip
     */
    public Builder offset(Integer offset) {
      this.offset = offset;
      return this;
    }

    /**
     * A flag to signal to the API to NOT make spelling corrections and to search for the given query no matter
     * how many results are returned. First set this to false, then return the corrected results but offer the user
     * the ability to say "no I actually meant to search for XXX, don't correct it this time". Then set this flag
     * to true.
     */
    public Builder skipCorrection(Boolean skipCorrection) {
      this.skipCorrection = skipCorrection;
      return this;
    }

    /**
     * Flag indicating if you want to restrict your search to gluten free items only
     */
    public Builder glutenFree(Boolean glutenFree) {
      this.glutenFree = glutenFree;
      return this;
    }

    /**
     * Flag indicating if you want to restrict you search to items without peanuts only
     */
    public Builder peanutFree(Boolean peanutFree) {
      this.peanutFree = peanutFree;
      return this;
    }

    @Override
    public SimpleSearchRequest build() {
      checkRequiredFields();
      return new SimpleSearchRequest(this);
    }
  }
}
