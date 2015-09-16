package com.roodie.materialmovies.util;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.google.common.base.Preconditions;
import com.roodie.model.entities.MovieWrapper;
import com.roodie.model.entities.ShowWrapper;
import com.roodie.model.state.DatabaseHelper;
import com.uwetrottmann.tmdb.entities.Movie;
import com.uwetrottmann.tmdb.entities.TvShowComplete;

import java.util.Collection;
import java.util.List;

import nl.qbusict.cupboard.DatabaseCompartment;
import nl.qbusict.cupboard.QueryResultIterable;

import static nl.qbusict.cupboard.CupboardFactory.cupboard;

/**
 * Created by Roodie on 24.06.2015.
 */
public class MMovieSQLiteOpenHelper extends SQLiteOpenHelper implements DatabaseHelper {

    private static String LOG_TAG = MMovieSQLiteOpenHelper.class.getSimpleName();

    private static final String DATABASE_NAME = "mMovie.db";

    private static final int DBVER_SHOWS = 2;
    private static final int DBVER_START = 1;

    public static final int DATABASE_VERSION = DBVER_SHOWS;


    static {
        // register our models
        cupboard().register(Movie.class);
        cupboard().register(TvShowComplete.class);
    }

    private boolean mIsClosed;

    public MMovieSQLiteOpenHelper(Context context) {
        // this will ensure that all tables are created
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
       cupboard().withDatabase(db).createTables();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(LOG_TAG, "Upgrading from " + oldVersion + " to " + newVersion);
        int version = oldVersion;
        switch (version) {
            case DBVER_START:
                cupboard().withDatabase(db).upgradeTables();
                version = DBVER_SHOWS;
        }

        Log.d(LOG_TAG, "After upgrade at version " + version);

        if (version != DATABASE_VERSION) {
            onResetDatabase(db);
        }
    }

    /**
     * Drops all tables and creates an empty database.
     */
    private void onResetDatabase(SQLiteDatabase db) {
        Log.d(LOG_TAG, "Resetting database");

        cupboard().withDatabase(db).dropAllTables();
        onCreate(db);
    }


    @Override
    public synchronized void close() {
        mIsClosed = true;
        super.close();
    }

    @Override
    public void put(MovieWrapper movie) {
        assetNotClosed();

        try {
            cupboard().withDatabase(getWritableDatabase()).put(movie);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    @Override
    public void putMovies(Collection<MovieWrapper> movies) {
        assetNotClosed();
        try {
            cupboard().withDatabase(getWritableDatabase()).put(movies);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void put(ShowWrapper show) {
        assetNotClosed();

        try {
            cupboard().withDatabase(getWritableDatabase()).put(show);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void putShows(Collection<ShowWrapper> shows) {
        assetNotClosed();
        try {
            cupboard().withDatabase(getWritableDatabase()).put(shows);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void deleteMovies(Collection<MovieWrapper> movies) {
        assetNotClosed();
        SQLiteDatabase db = null;

        try {
            db = getWritableDatabase();
            db.beginTransaction();
            final DatabaseCompartment dbc = cupboard().withDatabase(db);
            for (MovieWrapper movie : movies) {
                dbc.delete(movie);
            }
            db.setTransactionSuccessful();
        } catch ( Exception ex) {
            ex.printStackTrace();
        } finally {
            if (db != null) {
                db.endTransaction();
            }
        }
    }

    @Override
    public void deleteShows(Collection<ShowWrapper> shows) {
        assetNotClosed();
        SQLiteDatabase db = null;

        try {
            db = getWritableDatabase();
            db.beginTransaction();
            final DatabaseCompartment dbc = cupboard().withDatabase(db);
            for (ShowWrapper show : shows) {
                dbc.delete(show);
            }
            db.setTransactionSuccessful();
        } catch ( Exception ex) {
            ex.printStackTrace();
        } finally {
            if (db != null) {
                db.endTransaction();
            }
        }
    }

    @Override
    public void deleteAllMovies() {
        deleteAllMovies(getWritableDatabase());
    }

    @Override
    public void deleteAllShows() {
        deleteAllShows(getWritableDatabase());
    }

    @Override
    public boolean isClosed() {
        return mIsClosed;
    }


    public void deleteAllMovies(SQLiteDatabase db) {
        assetNotClosed();
        try {

            final int numDeleted = cupboard().withDatabase(db).delete(Movie.class, null);
                Log.v(LOG_TAG, "delete all movies. Deleted " + numDeleted + " rows.");
        } catch (Exception ex) {
           ex.printStackTrace();
        }
    }

    public void deleteAllShows(SQLiteDatabase db) {
        assetNotClosed();
        try {

            final int numDeleted = cupboard().withDatabase(db).delete(TvShowComplete.class, null);
            Log.v(LOG_TAG, "delete all shows. Deleted " + numDeleted + " rows.");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void assetNotClosed() {
        Preconditions.checkState(!mIsClosed, "Database is closed");
    }

    private List<MovieWrapper> queryMovies(String selection, String... selectionArgs) {
        assetNotClosed();
        QueryResultIterable<MovieWrapper> iterable = null;
         try {
             iterable = cupboard().withDatabase(getReadableDatabase()).query(MovieWrapper.class)
                     .withSelection(selection, selectionArgs)
                     .query();
         } finally {
             if (iterable !=null) {
                 iterable.close();
                 iterable = null;
             }
         }
        return iterable != null ? iterable.list() : null;
    }


}
