package com.truckmuncher.truckmuncher.data.sql;

import java.util.ArrayList;
import java.util.List;

/**
 * Example:
 * SelectionQueryBuilder q = new SelectionQueryBuilder()
 * .expr("is_awesome", EQ, true)
 * .expr("money", GT, 50.0f)
 * .expr("speed", LT, 21.1f)
 * .expr("door_number", EQ, 123)
 * .opt("apples", EQ, 0);
 */
public class SelectionQueryBuilder {
    private static final String AND = " AND ";
    private static final String OR = " OR ";
    private StringBuilder mBuilder;
    private List<String> mArgs = new ArrayList<String>();
    private String mNextOp = null;

    public SelectionQueryBuilder() {
        mBuilder = new StringBuilder();
    }

    public List<String> getArgs() {
        return mArgs;
    }

    public String[] getArgsArray() {
        return mArgs.toArray(new String[0]);
    }

    public SelectionQueryBuilder expr(String column, String op, String arg) {
        ensureOp();
        mBuilder.append(column).append(op).append("?");
        mArgs.add(arg);
        mNextOp = null;

        return this;
    }

    public SelectionQueryBuilder expr(SelectionQueryBuilder builder) {

        List<String> args = builder.getArgs();


        if (args.size() > 0) {
            ensureOp();
            mBuilder.append("(").append(builder).append(")");
            mArgs.addAll(args);
        }

        mNextOp = null;

        return this;
    }

    public SelectionQueryBuilder expr(String column, String op, boolean arg) {
        return expr(column, op, arg ? "1" : "0");
    }

    public SelectionQueryBuilder expr(String column, String op, int arg) {
        return expr(column, op, String.valueOf(arg));
    }

    public SelectionQueryBuilder expr(String column, String op, long arg) {
        return expr(column, op, String.valueOf(arg));
    }

    public SelectionQueryBuilder expr(String column, String op, float arg) {
        return expr(column, op, String.valueOf(arg));
    }

    public SelectionQueryBuilder expr(String column, String op, double arg) {
        return expr(column, op, String.valueOf(arg));
    }

    public SelectionQueryBuilder optExpr(String column, String op, String arg) {
        if (arg == null) {
            return this;
        }

        return expr(column, op, arg);
    }

    public SelectionQueryBuilder optExpr(String column, String op, boolean arg) {
        if (!arg) {
            return this;
        }

        return expr(column, op, arg ? "1" : "0");
    }

    public SelectionQueryBuilder opt(String column, String op, int arg) {
        if (arg == 0) {
            return this;
        }
        return expr(column, op, String.valueOf(arg));
    }

    public SelectionQueryBuilder opt(String column, String op, boolean arg) {
        if (!arg) {
            return this;
        }
        return expr(column, op, String.valueOf(arg));
    }

    public SelectionQueryBuilder opt(String column, String op, long arg) {
        if (arg == 0) {
            return this;
        }
        return expr(column, op, String.valueOf(arg));
    }

    public SelectionQueryBuilder opt(String column, String op, float arg) {
        if (arg == 0) {
            return this;
        }
        return expr(column, op, String.valueOf(arg));
    }

    public SelectionQueryBuilder opt(String column, String op, double arg) {
        if (arg == 0) {
            return this;
        }
        return expr(column, op, String.valueOf(arg));
    }

    public SelectionQueryBuilder and() {
        mNextOp = AND;

        return this;
    }

    public SelectionQueryBuilder or() {
        mNextOp = OR;

        return this;
    }

    private void ensureOp() {
        if (mBuilder.length() == 0) {
            return;
        }

        if (mNextOp == null) {
            mBuilder.append(AND);
        } else {
            mBuilder.append(mNextOp);
            mNextOp = null;
        }
    }

    @Override
    public String toString() {
        return mBuilder.toString();
    }

    public interface Op {
        public String EQ = " = ";
        public String NEQ = " != ";
        public String GT = " > ";
        public String LT = " < ";
        public String GTEQ = " >= ";
        public String LTEQ = " <= ";
        public String LIKE = " LIKE ";
        public String IS = " IS ";
        public String ISNOT = " IS NOT ";
        public String REGEXP = " REGEXP ";
    }
}
