// Code generated by Wire protocol buffer compiler, do not edit.
// Source file: /Volumes/home/projects/TruckMuncher-Protos/com/truckmuncher/api/search.proto
package com.truckmuncher.api.search;

import com.squareup.wire.Message;
import com.squareup.wire.ProtoField;
import java.util.Collections;
import java.util.List;

import static com.squareup.wire.Message.Datatype.STRING;
import static com.squareup.wire.Message.Label.REPEATED;
import static com.squareup.wire.Message.Label.REQUIRED;

public final class SimpleSearchResponse extends Message {

  public static final List<SearchResponse> DEFAULT_SEARCHRESPONSE = Collections.emptyList();
  public static final String DEFAULT_CORRECTEDQUERY = "";
  public static final List<String> DEFAULT_SUGGESTIONS = Collections.emptyList();

  @ProtoField(tag = 1, label = REPEATED)
  public final List<SearchResponse> searchResponse;

  /**
   * The actual query used. May differ from the request query due to spelling corrections.
   * Use this to replace the query in the search bar and then offer suggestions
   */
  @ProtoField(tag = 2, type = STRING, label = REQUIRED)
  public final String correctedQuery;

  /**
   * A list of suggested spelling alternatives. This will be populated if your query was corrected and there are less than 10 results.
   * These will be in order of most likely first to least likely last
   */
  @ProtoField(tag = 3, type = STRING, label = REPEATED)
  public final List<String> suggestions;

  public SimpleSearchResponse(List<SearchResponse> searchResponse, String correctedQuery, List<String> suggestions) {
    this.searchResponse = immutableCopyOf(searchResponse);
    this.correctedQuery = correctedQuery;
    this.suggestions = immutableCopyOf(suggestions);
  }

  private SimpleSearchResponse(Builder builder) {
    this(builder.searchResponse, builder.correctedQuery, builder.suggestions);
    setBuilder(builder);
  }

  @Override
  public boolean equals(Object other) {
    if (other == this) return true;
    if (!(other instanceof SimpleSearchResponse)) return false;
    SimpleSearchResponse o = (SimpleSearchResponse) other;
    return equals(searchResponse, o.searchResponse)
        && equals(correctedQuery, o.correctedQuery)
        && equals(suggestions, o.suggestions);
  }

  @Override
  public int hashCode() {
    int result = hashCode;
    if (result == 0) {
      result = searchResponse != null ? searchResponse.hashCode() : 1;
      result = result * 37 + (correctedQuery != null ? correctedQuery.hashCode() : 0);
      result = result * 37 + (suggestions != null ? suggestions.hashCode() : 1);
      hashCode = result;
    }
    return result;
  }

  public static final class Builder extends Message.Builder<SimpleSearchResponse> {

    public List<SearchResponse> searchResponse;
    public String correctedQuery;
    public List<String> suggestions;

    public Builder() {
    }

    public Builder(SimpleSearchResponse message) {
      super(message);
      if (message == null) return;
      this.searchResponse = copyOf(message.searchResponse);
      this.correctedQuery = message.correctedQuery;
      this.suggestions = copyOf(message.suggestions);
    }

    public Builder searchResponse(List<SearchResponse> searchResponse) {
      this.searchResponse = checkForNulls(searchResponse);
      return this;
    }

    /**
     * The actual query used. May differ from the request query due to spelling corrections.
     * Use this to replace the query in the search bar and then offer suggestions
     */
    public Builder correctedQuery(String correctedQuery) {
      this.correctedQuery = correctedQuery;
      return this;
    }

    /**
     * A list of suggested spelling alternatives. This will be populated if your query was corrected and there are less than 10 results.
     * These will be in order of most likely first to least likely last
     */
    public Builder suggestions(List<String> suggestions) {
      this.suggestions = checkForNulls(suggestions);
      return this;
    }

    @Override
    public SimpleSearchResponse build() {
      checkRequiredFields();
      return new SimpleSearchResponse(this);
    }
  }
}
