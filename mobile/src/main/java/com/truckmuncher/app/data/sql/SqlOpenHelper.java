package com.truckmuncher.app.data.sql;

import android.content.Context;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.NonNull;
import android.support.annotation.RawRes;

import com.truckmuncher.app.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import timber.log.Timber;

public abstract class SqlOpenHelper extends SQLiteOpenHelper {

    public static final int VERSION = 2;

    private final Context context;

    protected SqlOpenHelper(Context context, String dbName, int version) {
        super(context, dbName, null, version);
        this.context = context.getApplicationContext();
    }

    public static SqlOpenHelper newInstance(Context context) {
        return new SqlOpenHelperImpl(context);
    }

    @Override
    public final void onCreate(@NonNull SQLiteDatabase db) {

        // Create structure
        readAndExecuteSQLScript(db, context, R.raw.truckmuncher);

        // Apply any migration found. This way we don't have to rewrite the original and lose out history
        onUpgrade(db, 1, VERSION);
    }

    @Override
    public final void onUpgrade(@NonNull SQLiteDatabase db, int oldVersion, int newVersion) {
        for (int i = oldVersion; i < newVersion; ++i) {
            String migrationFileName = String.format("from_%d_to_%d", i, (i + 1));
            Timber.d("Looking for migration file: %s", migrationFileName);
            int migrationFileResId = context.getResources().getIdentifier(migrationFileName, "raw", context.getPackageName());

            if (migrationFileResId != 0) {

                // execute script
                readAndExecuteSQLScript(db, context, migrationFileResId);

            } else {
                Timber.d("Not found: %s", migrationFileName);
            }
        }
    }

    private void readAndExecuteSQLScript(SQLiteDatabase db, Context context, @RawRes int sqlScriptResId) {
        Resources res = context.getResources();

        try {
            InputStream is = res.openRawResource(sqlScriptResId);
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));

            executeSQLScript(db, reader);

            reader.close();
            is.close();

        } catch (IOException e) {
            throw new RuntimeException("Unable to read SQL script", e);
        }
    }

    private void executeSQLScript(SQLiteDatabase db, BufferedReader reader) throws IOException {
        String line;
        StringBuilder statement = new StringBuilder();
        while ((line = reader.readLine()) != null) {
            statement.append(line);
            statement.append("\n");
            if (line.endsWith(";")) {
                String toExec = statement.toString();
                Timber.d("Executing script: %s", toExec);
                db.execSQL(toExec);
                statement = new StringBuilder();
            }
        }
    }

    private static class SqlOpenHelperImpl extends SqlOpenHelper {

        private static final String NAME = "truckmuncher.db";

        private SqlOpenHelperImpl(Context context) {
            super(context, NAME, VERSION);
        }
    }
}
