package com.truckmuncher.app.test;

import android.net.Uri;

import org.assertj.core.api.AbstractAssert;

import static org.assertj.core.api.Assertions.assertThat;

public class UriAssert extends AbstractAssert<UriAssert, Uri> {

    protected UriAssert(Uri actual) {
        super(actual, UriAssert.class);
    }

    public UriAssert hasContentScheme() {
        isNotNull();

        assertThat(actual.getScheme())
                .overridingErrorMessage("Expected scheme <content> but found <%s>", actual.getScheme().toLowerCase())
                .isEqualToIgnoringCase("content");
        return this;
    }

    public UriAssert hasAuthority(String authority) {
        isNotNull();

        assertThat(actual.getAuthority())
                .overridingErrorMessage("Expected authority <%s> but found <%s>", authority.toLowerCase(), actual.getAuthority().toLowerCase())
                .isEqualToIgnoringCase(authority);
        return this;
    }

    public UriAssert hasPath(String path) {
        isNotNull();

        assertThat(actual.getPath())
                .overridingErrorMessage("Expected path <%s> but found <%s>", path.toLowerCase(), actual.getPath().toLowerCase())
                .isEqualToIgnoringCase(path);
        return this;
    }

}
