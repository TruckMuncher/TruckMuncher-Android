// Code generated by Wire protocol buffer compiler, do not edit.
// Source file: /Volumes/home/projects/TruckMuncher-Protos/com/truckmuncher/api/trucks.proto
package com.truckmuncher.api.trucks;

import com.squareup.wire.Message;
import com.squareup.wire.ProtoField;
import java.util.Collections;
import java.util.List;

import static com.squareup.wire.Message.Datatype.BOOL;
import static com.squareup.wire.Message.Datatype.STRING;
import static com.squareup.wire.Message.Label.REPEATED;

public final class Truck extends Message {

  public static final String DEFAULT_ID = "";
  public static final String DEFAULT_NAME = "";
  public static final String DEFAULT_IMAGEURL = "";
  public static final List<String> DEFAULT_KEYWORDS = Collections.emptyList();
  public static final String DEFAULT_PRIMARYCOLOR = "";
  public static final String DEFAULT_SECONDARYCOLOR = "";
  public static final String DEFAULT_DESCRIPTION = "";
  public static final String DEFAULT_PHONENUMBER = "";
  public static final Boolean DEFAULT_APPROVED = false;
  public static final Boolean DEFAULT_APPROVALPENDING = false;
  public static final String DEFAULT_WEBSITE = "";

  /**
   * Suitable for unique identification. Will always be set on a response from the API.
   */
  @ProtoField(tag = 1, type = STRING)
  public final String id;

  @ProtoField(tag = 2, type = STRING)
  public final String name;

  @ProtoField(tag = 3, type = STRING)
  public final String imageUrl;

  /**
   * These are likely going to be the cuisines the truck targets, but might also be something like "soup", "panini", or "vegan"
   */
  @ProtoField(tag = 4, type = STRING, label = REPEATED)
  public final List<String> keywords;

  /**
   * Hex code in the format of #RRGGBB
   */
  @ProtoField(tag = 5, type = STRING)
  public final String primaryColor;

  /**
   * Hex code in the format of #RRGGBB
   */
  @ProtoField(tag = 6, type = STRING)
  public final String secondaryColor;

  /**
   * A free-form description of the truck
   */
  @ProtoField(tag = 7, type = STRING)
  public final String description;

  /**
   * Should be in the format of (xxx) xxx-xxxx.
   */
  @ProtoField(tag = 8, type = STRING)
  public final String phoneNumber;

  @ProtoField(tag = 9, type = BOOL)
  public final Boolean approved;

  @ProtoField(tag = 10, type = BOOL)
  public final Boolean approvalPending;

  @ProtoField(tag = 11, type = STRING)
  public final String website;

  public Truck(String id, String name, String imageUrl, List<String> keywords, String primaryColor, String secondaryColor, String description, String phoneNumber, Boolean approved, Boolean approvalPending, String website) {
    this.id = id;
    this.name = name;
    this.imageUrl = imageUrl;
    this.keywords = immutableCopyOf(keywords);
    this.primaryColor = primaryColor;
    this.secondaryColor = secondaryColor;
    this.description = description;
    this.phoneNumber = phoneNumber;
    this.approved = approved;
    this.approvalPending = approvalPending;
    this.website = website;
  }

  private Truck(Builder builder) {
    this(builder.id, builder.name, builder.imageUrl, builder.keywords, builder.primaryColor, builder.secondaryColor, builder.description, builder.phoneNumber, builder.approved, builder.approvalPending, builder.website);
    setBuilder(builder);
  }

  @Override
  public boolean equals(Object other) {
    if (other == this) return true;
    if (!(other instanceof Truck)) return false;
    Truck o = (Truck) other;
    return equals(id, o.id)
        && equals(name, o.name)
        && equals(imageUrl, o.imageUrl)
        && equals(keywords, o.keywords)
        && equals(primaryColor, o.primaryColor)
        && equals(secondaryColor, o.secondaryColor)
        && equals(description, o.description)
        && equals(phoneNumber, o.phoneNumber)
        && equals(approved, o.approved)
        && equals(approvalPending, o.approvalPending)
        && equals(website, o.website);
  }

  @Override
  public int hashCode() {
    int result = hashCode;
    if (result == 0) {
      result = id != null ? id.hashCode() : 0;
      result = result * 37 + (name != null ? name.hashCode() : 0);
      result = result * 37 + (imageUrl != null ? imageUrl.hashCode() : 0);
      result = result * 37 + (keywords != null ? keywords.hashCode() : 1);
      result = result * 37 + (primaryColor != null ? primaryColor.hashCode() : 0);
      result = result * 37 + (secondaryColor != null ? secondaryColor.hashCode() : 0);
      result = result * 37 + (description != null ? description.hashCode() : 0);
      result = result * 37 + (phoneNumber != null ? phoneNumber.hashCode() : 0);
      result = result * 37 + (approved != null ? approved.hashCode() : 0);
      result = result * 37 + (approvalPending != null ? approvalPending.hashCode() : 0);
      result = result * 37 + (website != null ? website.hashCode() : 0);
      hashCode = result;
    }
    return result;
  }

  public static final class Builder extends Message.Builder<Truck> {

    public String id;
    public String name;
    public String imageUrl;
    public List<String> keywords;
    public String primaryColor;
    public String secondaryColor;
    public String description;
    public String phoneNumber;
    public Boolean approved;
    public Boolean approvalPending;
    public String website;

    public Builder() {
    }

    public Builder(Truck message) {
      super(message);
      if (message == null) return;
      this.id = message.id;
      this.name = message.name;
      this.imageUrl = message.imageUrl;
      this.keywords = copyOf(message.keywords);
      this.primaryColor = message.primaryColor;
      this.secondaryColor = message.secondaryColor;
      this.description = message.description;
      this.phoneNumber = message.phoneNumber;
      this.approved = message.approved;
      this.approvalPending = message.approvalPending;
      this.website = message.website;
    }

    /**
     * Suitable for unique identification. Will always be set on a response from the API.
     */
    public Builder id(String id) {
      this.id = id;
      return this;
    }

    public Builder name(String name) {
      this.name = name;
      return this;
    }

    public Builder imageUrl(String imageUrl) {
      this.imageUrl = imageUrl;
      return this;
    }

    /**
     * These are likely going to be the cuisines the truck targets, but might also be something like "soup", "panini", or "vegan"
     */
    public Builder keywords(List<String> keywords) {
      this.keywords = checkForNulls(keywords);
      return this;
    }

    /**
     * Hex code in the format of #RRGGBB
     */
    public Builder primaryColor(String primaryColor) {
      this.primaryColor = primaryColor;
      return this;
    }

    /**
     * Hex code in the format of #RRGGBB
     */
    public Builder secondaryColor(String secondaryColor) {
      this.secondaryColor = secondaryColor;
      return this;
    }

    /**
     * A free-form description of the truck
     */
    public Builder description(String description) {
      this.description = description;
      return this;
    }

    /**
     * Should be in the format of (xxx) xxx-xxxx.
     */
    public Builder phoneNumber(String phoneNumber) {
      this.phoneNumber = phoneNumber;
      return this;
    }

    public Builder approved(Boolean approved) {
      this.approved = approved;
      return this;
    }

    public Builder approvalPending(Boolean approvalPending) {
      this.approvalPending = approvalPending;
      return this;
    }

    public Builder website(String website) {
      this.website = website;
      return this;
    }

    @Override
    public Truck build() {
      return new Truck(this);
    }
  }
}
