package com.truckmuncher.truckmuncher.customer;

import android.graphics.Color;

public final class ColorCorrector {

    private ColorCorrector() {
    }

    static int calculateTextColor(String backgroundHex) {
        int color = Color.parseColor(backgroundHex);
        int d;

        // Counting the perceptive luminance - human eye favors green color...
        double a = 1 - (0.299 * Color.red(color) + 0.587 * Color.green(color) + 0.114 * Color.blue(color)) / 255;

        if (a < 0.5) {
            d = 0; // bright colors - black font
        } else {
            d = 255; // dark colors - white font
        }

        return Color.rgb(d, d, d);
    }
}
