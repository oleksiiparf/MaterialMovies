package com.roodie.model.state;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.google.common.base.Preconditions;
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
    public void put(Movie movie) {
        assetNotClosed();

        try {
            cupboard().withDatabase(getWritableDatabase()).put(movie);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    @Override
    public void put(Collection<Movie> movies) {
        assetNotClosed();
        try {
            cupboard().withDatabase(getWritableDatabase()).put(movies);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    @Override
    public List<Movie> getWatchList() {
        return queryMovies("", "");
    }

    @Override
    public void delete(Collection<Movie> movies) {
        assetNotClosed();
        SQLiteDatabase db = null;

        try {
            db = getWritableDatabase();
            db.beginTransaction();
            final DatabaseCompartment dbc = cupboard().withDatabase(db);
            for (Movie movie : movies) {
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

    private List<Movie> queryMovies(String selection, String... selectionArgs) {
        assetNotClosed();
        QueryResultIterable<Movie> iterable = null;
         try {
             iterable = cupboard().withDatabase(getReadableDatabase()).query(Movie.class)
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
