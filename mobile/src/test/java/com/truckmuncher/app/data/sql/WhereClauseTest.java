/*
 * Copyright 2014-2015 Marius Volkhart
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
 *
 * Original at https://gist.github.com/MariusVolkhart/3e2374b5fdbefad17d56
 */

package com.truckmuncher.app.data.sql;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class WhereClauseTest {

    @Test
    public void operator_values_are_correct() {
        assertThat(WhereClause.Operator.EQUALS).isEqualTo("=");
        assertThat(WhereClause.Operator.NOT_EQUALS).isEqualTo("!=");
        assertThat(WhereClause.Operator.GREATER_THAN).isEqualTo(">");
        assertThat(WhereClause.Operator.LESS_THAN).isEqualTo("<");
        assertThat(WhereClause.Operator.GREATER_THAN_EQUALS).isEqualTo(">=");
        assertThat(WhereClause.Operator.LESS_THAN_EQUALS).isEqualTo("<=");
        assertThat(WhereClause.Operator.LIKE).isEqualTo(" LIKE ");
        assertThat(WhereClause.Operator.IS).isEqualTo(" IS ");
        assertThat(WhereClause.Operator.IS_NOT).isEqualTo(" IS NOT ");
    }

    @Test
    public void where_boolean_is_true() {
        WhereClause whereClause = new WhereClause.Builder()
                .where("isDirty", WhereClause.Operator.EQUALS, true)
                .build();

        assertThat(whereClause.selection).isEqualTo("isDirty=?");
        assertThat(whereClause.selectionArgs).containsExactly("1");
    }

    @Test
    public void where_boolean_is_false() {
        WhereClause whereClause = new WhereClause.Builder()
                .where("isDirty", WhereClause.Operator.EQUALS, false)
                .build();

        assertThat(whereClause.selection).isEqualTo("isDirty=?");
        assertThat(whereClause.selectionArgs).containsExactly("0");
    }

    @Test
    public void where_integer() {
        WhereClause whereClause = new WhereClause.Builder()
                .where("age", WhereClause.Operator.EQUALS, 23)
                .build();

        assertThat(whereClause.selection).isEqualTo("age=?");
        assertThat(whereClause.selectionArgs).containsExactly("23");
    }

    @Test
    public void where_long() {
        WhereClause whereClause = new WhereClause.Builder()
                .where("id", WhereClause.Operator.EQUALS, Long.MAX_VALUE)
                .build();

        assertThat(whereClause.selection).isEqualTo("id=?");
        assertThat(whereClause.selectionArgs).containsExactly(Long.toString(Long.MAX_VALUE));
    }

    @Test
    public void where_float() {
        WhereClause whereClause = new WhereClause.Builder()
                .where("price", WhereClause.Operator.EQUALS, 9.99f)
                .build();

        assertThat(whereClause.selection).isEqualTo("price=?");
        assertThat(whereClause.selectionArgs).containsExactly("9.99");
    }

    @Test
    public void where_double() {
        WhereClause whereClause = new WhereClause.Builder()
                .where("price", WhereClause.Operator.EQUALS, 9.99)
                .build();

        assertThat(whereClause.selection).isEqualTo("price=?");
        assertThat(whereClause.selectionArgs).containsExactly("9.99");
    }

    @Test
    public void where_string() {
        WhereClause whereClause = new WhereClause.Builder()
                .where("name", WhereClause.Operator.EQUALS, "John")
                .build();

        assertThat(whereClause.selection).isEqualTo("name=?");
        assertThat(whereClause.selectionArgs).containsExactly("John");
    }

    @Test
    public void and() {
        WhereClause whereClause = new WhereClause.Builder()
                .where("first_name", WhereClause.Operator.EQUALS, "John")
                .and()
                .where("last_name", WhereClause.Operator.EQUALS, "Doe")
                .build();

        assertThat(whereClause.selection).isEqualTo("first_name=? AND last_name=?");
        assertThat(whereClause.selectionArgs).containsExactly("John", "Doe");
    }

    @Test
    public void or() {
        WhereClause whereClause = new WhereClause.Builder()
                .where("first_name", WhereClause.Operator.EQUALS, "John")
                .or()
                .where("first_name", WhereClause.Operator.EQUALS, "Jane")
                .build();

        assertThat(whereClause.selection).isEqualTo("first_name=? OR first_name=?");
        assertThat(whereClause.selectionArgs).containsExactly("John", "Jane");
    }

    @Test
    public void implicit_and_on_multiple_wheres() {
        WhereClause whereClause = new WhereClause.Builder()
                .where("first_name", WhereClause.Operator.EQUALS, "John")
                .where("last_name", WhereClause.Operator.EQUALS, "Doe")
                .build();

        assertThat(whereClause.selection).isEqualTo("first_name=? AND last_name=?");
        assertThat(whereClause.selectionArgs).containsExactly("John", "Doe");
    }

    @Test
    public void nested_whereClause() {
        WhereClause john = new WhereClause.Builder()
                .where("first_name", WhereClause.Operator.EQUALS, "John")
                .where("last_name", WhereClause.Operator.EQUALS, "Doe")
                .build();

        WhereClause jane = new WhereClause.Builder()
                .where("first_name", WhereClause.Operator.EQUALS, "Jane")
                .where("last_name", WhereClause.Operator.EQUALS, "Doe")
                .build();

        WhereClause whereClause = new WhereClause.Builder()
                .where(john)
                .or()
                .where(jane)
                .build();

        assertThat(whereClause.selection).isEqualTo("(first_name=? AND last_name=?) OR (first_name=? AND last_name=?)");
        assertThat(whereClause.selectionArgs).containsExactly("John", "Doe", "Jane", "Doe");
    }

    @Test
    public void toStringUsesCorrectClassName() {
        assertThat(new WhereClause.Builder().build().toString()).startsWith("WhereClause");
    }
}
