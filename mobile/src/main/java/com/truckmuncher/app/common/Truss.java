/*
 * Original file at https://gist.github.com/JakeWharton/11274467
 *
 * Licensed under Apache License, Version 2.0
 */
package com.truckmuncher.app.common;

import android.text.SpannableStringBuilder;

import java.util.ArrayDeque;
import java.util.Deque;

import static android.text.Spanned.SPAN_INCLUSIVE_EXCLUSIVE;

/**
 * A {@link SpannableStringBuilder} wrapper whose API doesn't make me want to stab my eyes out.
 * <p/>
 * Example usage:
 * <br> {@code Truss t = new Truss()}
 * <br> {@code .append("Thanks Jake, ")}
 * <br> {@code .pushSpan(new StyleSpan(android.graphics.Typeface.BOLD))}
 * <br> {@code .append("Truss saves time")}
 * <br> {@code .popSpan()}
 * <br> {@code .append(" and you are great.");}
 * <br> {@code TextView name = (TextView)findViewById(R.id.tvname);}
 * <br> {@code name.setText(t.build());}
 * <p/>
 * <br>results in:<br> Thanks Jake, <b>Truss saves time</b> and you are great.
 */
public class Truss {
    private final SpannableStringBuilder builder;
    private final Deque<Span> stack;

    public Truss() {
        builder = new SpannableStringBuilder();
        stack = new ArrayDeque<>();
    }

    public Truss append(String string) {
        builder.append(string);
        return this;
    }

    public Truss append(CharSequence charSequence) {
        builder.append(charSequence);
        return this;
    }

    public Truss append(char c) {
        builder.append(c);
        return this;
    }

    public Truss append(int number) {
        builder.append(String.valueOf(number));
        return this;
    }

    /**
     * Starts {@code span} at the current position in the builder.
     */
    public Truss pushSpan(Object span) {
        stack.addLast(new Span(builder.length(), span));
        return this;
    }

    /**
     * End the most recently pushed span at the current position in the builder.
     */
    public Truss popSpan() {
        Span span = stack.removeLast();
        builder.setSpan(span.span, span.start, builder.length(), SPAN_INCLUSIVE_EXCLUSIVE);
        return this;
    }

    /**
     * Create the final {@link CharSequence}, popping any remaining spans.
     */
    public CharSequence build() {
        while (!stack.isEmpty()) {
            popSpan();
        }
        return builder; // TODO make immutable copy?
    }

    private static final class Span {
        final int start;
        final Object span;

        public Span(int start, Object span) {
            this.start = start;
            this.span = span;
        }
    }
}