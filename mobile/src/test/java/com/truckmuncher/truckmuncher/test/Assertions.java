package com.truckmuncher.truckmuncher.test;

import android.net.Uri;

public class Assertions {

    public static UriAssert assertThat(Uri actual) {
        return new UriAssert(actual);
    }
}
