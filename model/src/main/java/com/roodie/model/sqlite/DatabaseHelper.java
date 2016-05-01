package com.roodie.model.sqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by Roodie on 11.03.2016.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String TAG = DatabaseHelper.class.getSimpleName();

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "database";

   // private static final DatabaseHelper INSTANCE = new DatabaseHelper();
    private Set<String> createSQLs = new HashSet<>();
    private Set<String> dropSQLs = new HashSet<>();

   /* public static DatabaseHelper get() {
        return INSTANCE;
    }*/

    private DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void addCreateSQL(String sql) {
        createSQLs.add(sql);
    }

    public void addDropSQL(String sql) {
        dropSQLs.add(sql);
    }

    /**
     * @see android.database.sqlite.SQLiteOpenHelper#onCreate(android.database.sqlite.SQLiteDatabase)
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        for (String createSQL : createSQLs) {
            db.execSQL(createSQL);
        }
    }

    /**
     * @see android.database.sqlite.SQLiteOpenHelper#onUpgrade(android.database.sqlite.SQLiteDatabase, int, int)
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(TAG, "Upgrading database from version " + oldVersion + " to " + newVersion
                + ", which will destroy all old data");
        for (String dropSQL : dropSQLs) {
            db.execSQL(dropSQL);
        }
        onCreate(db);
    }
}