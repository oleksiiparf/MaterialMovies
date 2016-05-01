package com.roodie.model.sqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.roodie.model.util.Lists;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Roodie on 11.03.2016.
 */
public class SQLiteHelper extends SQLiteOpenHelper {

    private final static String DB_NAME = "mmovies.db";

    private static final int DATABASE_VERSION = 2;

    private Set<String> createSQLs = new HashSet<>();
    private List<SQLiteUpgradeStep> upgradeSteps = Lists.newArrayList();

    public SQLiteHelper(Context context) {
        super(context, DB_NAME, null, DATABASE_VERSION);
    }

    /**
     * Add a creation SQL statement to be executed in {@link SQLiteHelper#onCreate(SQLiteDatabase)} method.
     *
     * @param sql creation statement
     */
    public void addCreateSQL(String sql) {
        if (sql != null) {
            createSQLs.add(sql);
        }
    }

    /**
     * Add a {@link SQLiteUpgradeStep} to be executed in {@link SQLiteHelper#onUpgrade(SQLiteDatabase, int, int)}
     * method.
     *
     * @param upgradeStep upgrade steps to add.
     */
    public void addUpgradeStep(SQLiteUpgradeStep upgradeStep) {
        upgradeSteps.add(upgradeStep);
    }

    public void addUpgradeSteps(List<SQLiteUpgradeStep> upgradeSteps) {
        this.upgradeSteps.addAll(upgradeSteps);
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
        //LOGGER.debug("Upgrading DB from version " + oldVersion + " to " + newVersion);
        for (SQLiteUpgradeStep upgradeStep : upgradeSteps) {
            if (upgradeStep.getVersion() > oldVersion) {
                //LOGGER.debug("Executing upgrade step " + upgradeStep.getClass().getSimpleName());
                upgradeStep.upgrade(db, oldVersion, newVersion);
            }
        }
    }

    /**
     * @see android.database.sqlite.SQLiteOpenHelper#onOpen(android.database.sqlite.SQLiteDatabase)
     */
    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        if (!db.isReadOnly()) {
            // Enable foreign key constraints support
            db.execSQL("PRAGMA foreign_keys=ON;");
        }
    }

    /**
     * Verify if database file exits.
     *
     * @param context context
     * @return true if the file exits
     */
    public static boolean existDatabase(Context context) {
        return getDatabaseFile(context).exists();
    }

    public static File getDatabaseFile(Context context) {
        return context.getApplicationContext().getDatabasePath(DB_NAME);
    }
}