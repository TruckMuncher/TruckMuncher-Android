/*
 * Copyright 2014 Marius Volkhart
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.truckmuncher.truckmuncher.data.sql;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Representation of a constructed SQL query
 */
public final class Query {

    public final String selection;
    public final String[] selectionArgs;

    private Query(String selection, String[] selectionArgs) {
        this.selection = selection;
        this.selectionArgs = selectionArgs;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Query that = (Query) o;

        return !(selection != null ? !selection.equals(that.selection) : that.selection != null)
                && Arrays.equals(selectionArgs, that.selectionArgs);

    }

    @Override
    public int hashCode() {
        int result = selection != null ? selection.hashCode() : 0;
        result = 31 * result + (selectionArgs != null ? Arrays.hashCode(selectionArgs) : 0);
        return result;
    }

    @Override
    public String toString() {
        return "SelectionQuery{" +
                "selection='" + selection + '\'' +
                ", selectionArgs=" + Arrays.toString(selectionArgs) +
                '}';
    }

    public static class Builder {

        private static final String AND = " AND ";
        private static final String OR = " OR ";

        private final StringBuilder stringBuilder = new StringBuilder();
        private final List<String> args = new ArrayList<>();
        private String nextOperator;

        public Builder where(String column, String operand, String arg) {
            setNextOperatorIfNeeded();
            stringBuilder.append(column).append(operand).append("?");
            args.add(arg);
            nextOperator = null;

            return this;
        }

        public Builder where(String column, String operand, boolean arg) {
            return where(column, operand, arg ? "1" : "0");
        }

        public Builder where(String column, String operand, int arg) {
            return where(column, operand, Integer.toString(arg));
        }

        public Builder where(String column, String operand, long arg) {
            return where(column, operand, Long.toString(arg));
        }

        public Builder where(String column, String operand, float arg) {
            return where(column, operand, Float.toString(arg));
        }

        public Builder where(String column, String operand, double arg) {
            return where(column, operand, Double.toString(arg));
        }

        public Builder and() {
            nextOperator = AND;
            return this;
        }

        public Builder or() {
            nextOperator = OR;
            return this;
        }

        /**
         * Ensures that multiple {@code where} statements can be joined safely. Defaults to using
         * {@code AND}.
         */
        private void setNextOperatorIfNeeded() {
            if (stringBuilder.length() == 0) {
                return;
            }

            if (nextOperator == null) {
                stringBuilder.append(AND);
            } else {
                stringBuilder.append(nextOperator);
                nextOperator = null;
            }
        }

        public Query build() {
            String[] arguments = args.toArray(new String[args.size()]);
            return new Query(stringBuilder.toString(), arguments);
        }
    }

    public static class Builder {

        private static final String AND = " AND ";
        private static final String OR = " OR ";

        private final StringBuilder stringBuilder = new StringBuilder();
        private final List<String> args = new ArrayList<>();
        private String nextOperator;

        public Builder where(String column, String operand, String arg) {
            setNextOperatorIfNeeded();
            stringBuilder.append(column).append(operand).append("?");
            args.add(arg);
            nextOperator = null;

            return this;
        }

        public Builder where(String column, String operand, boolean arg) {
            return where(column, operand, arg ? "1" : "0");
        }

        public Builder where(String column, String operand, int arg) {
            return where(column, operand, Integer.toString(arg));
        }

        public Builder where(String column, String operand, long arg) {
            return where(column, operand, Long.toString(arg));
        }

        public Builder where(String column, String operand, float arg) {
            return where(column, operand, Float.toString(arg));
        }

        public Builder where(String column, String operand, double arg) {
            return where(column, operand, Double.toString(arg));
        }

        public Builder and() {
            nextOperator = AND;
            return this;
        }

        public Builder or() {
            nextOperator = OR;
            return this;
        }

        /**
         * Ensures that multiple {@code where} statements can be joined safely. Defaults to using
         * {@code AND}.
         */
        private void setNextOperatorIfNeeded() {
            if (stringBuilder.length() == 0) {
                return;
            }

            if (nextOperator == null) {
                stringBuilder.append(AND);
            } else {
                stringBuilder.append(nextOperator);
                nextOperator = null;
            }
        }

        public Query build() {
            String[] arguments = args.toArray(new String[args.size()]);
            return new Query(stringBuilder.toString(), arguments);
        }
    }

    /**
     * Common SQL operators. For convenience, consider implementing this interface if creating a
     * lot of queries.
     */
    public interface Operator {
        public String EQUALS = "=";
        public String NOT_EQUALS = "!=";
        public String GREATER_THAN = ">";
        public String LESS_THAN = "<";
        public String GREATER_THAN_EQUALS = ">=";
        public String LESS_THAN_EQUALS = "<=";
        public String LIKE = " LIKE ";
        public String IS = " IS ";
        public String IS_NOT = " IS NOT ";
    }
}
