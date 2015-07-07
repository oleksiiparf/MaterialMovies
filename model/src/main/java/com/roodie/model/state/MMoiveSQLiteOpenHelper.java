package com.roodie.model.state;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.google.common.base.Preconditions;
import com.roodie.model.entities.MovieWrapper;
import com.uwetrottmann.tmdb.entities.Movie;

import java.util.Collection;
import java.util.List;

import nl.qbusict.cupboard.DatabaseCompartment;
import nl.qbusict.cupboard.QueryResultIterable;

import static nl.qbusict.cupboard.CupboardFactory.cupboard;

/**
 * Created by Roodie on 24.06.2015.
 */
public class MMoiveSQLiteOpenHelper extends SQLiteOpenHelper implements DatabaseHelper {

    private static String LOG_TAG = MMoiveSQLiteOpenHelper.class.getSimpleName();

    private static final String DATABASE_NAME = "mMovie.db";
    private static final int DATABASE_VERSION = 1;

    static {
        cupboard().register(Movie.class);
    }

    private boolean mIsClosed;

    public MMoiveSQLiteOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
       cupboard().withDatabase(db).createTables();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
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
    public void put(Collection<MovieWrapper> movies) {
        assetNotClosed();
        try {
            cupboard().withDatabase(getWritableDatabase()).put(movies);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    @Override
    public List<MovieWrapper> getWatchList() {
        return queryMovies("", "");
    }

    @Override
    public void delete(Collection<MovieWrapper> movies) {
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
    public void deleteAllMovies() {
        deleteAllMovies(getWritableDatabase());
    }

    @Override
    public boolean isClosed() {
        return mIsClosed;
    }


    public void deleteAllMovies(SQLiteDatabase db) {
        assetNotClosed();
        try {

            final int numDeleted = cupboard().withDatabase(db).delete(Movie.class, null);
                Log.v(LOG_TAG, "deleteAllMovies. Deleted " + numDeleted + " rows.");
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
