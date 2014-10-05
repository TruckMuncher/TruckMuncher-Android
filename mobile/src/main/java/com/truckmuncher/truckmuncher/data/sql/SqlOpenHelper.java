package com.truckmuncher.truckmuncher.data.sql;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.NonNull;

public abstract class SqlOpenHelper extends SQLiteOpenHelper {

    public static final int VERSION = 1;

    protected SqlOpenHelper(Context context, String dbName, int version) {
        super(context, dbName, null, version);
    }

    public static SqlOpenHelper newInstance(Context context) {
        return new SqlOpenHelperImpl(context);
    }

    @Override
    public final void onCreate(@NonNull SQLiteDatabase db) {
        TruckTable.onCreate(db);
        CategoryTable.onCreate(db);
        MenuItemTable.onCreate(db);
        MenuView.onCreate(db);
    }

    @Override
    public final void onUpgrade(@NonNull SQLiteDatabase db, int oldVersion, int newVersion) {
        TruckTable.onUpgrade(db, oldVersion, newVersion);
        CategoryTable.onUpgrade(db, oldVersion, newVersion);
        MenuItemTable.onUpgrade(db, oldVersion, newVersion);
        MenuView.onUpgrade(db, oldVersion, newVersion);
    }

    private static class SqlOpenHelperImpl extends SqlOpenHelper {

        private static final String NAME = "truckmuncher.db";

        private SqlOpenHelperImpl(Context context) {
            super(context, NAME, VERSION);
        }
    }
}
