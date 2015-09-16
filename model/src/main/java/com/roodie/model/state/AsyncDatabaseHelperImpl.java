/*
 * Copyright 2014 Chris Banes
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.roodie.model.state;

import android.support.v4.util.ArrayMap;

import com.google.common.base.Preconditions;
import com.roodie.model.entities.MovieWrapper;
import com.roodie.model.entities.ShowWrapper;
import com.roodie.model.network.BackgroundCallRunnable;
import com.roodie.model.util.BackgroundExecutor;
import com.roodie.model.util.MoviesCollections;

import java.util.Collection;
import java.util.List;
import java.util.Map;


public class AsyncDatabaseHelperImpl implements AsyncDatabaseHelper {

    private final BackgroundExecutor mExecutor;
    private final DatabaseHelper mDbHelper;

    public AsyncDatabaseHelperImpl(BackgroundExecutor executor, DatabaseHelper dbHelper) {
        mExecutor = Preconditions.checkNotNull(executor, "executor cannot be null");
        mDbHelper = Preconditions.checkNotNull(dbHelper, "dbHelper cannot be null");
    }



    @Override
    public void putMovies(final Collection<MovieWrapper> movies) {
        mExecutor.execute(new DatabaseBackgroundRunnable<Void>() {
            @Override
            public Void doDatabaseCall(DatabaseHelper dbHelper) {
                dbHelper.deleteMovies(movies);
                return null;
            }
        });
    }

    @Override
    public void put(final MovieWrapper movie) {
        mExecutor.execute(new DatabaseBackgroundRunnable<Void>() {
            @Override
            public Void doDatabaseCall(DatabaseHelper dbHelper) {
                dbHelper.put(movie);
                return null;
            }
        });
    }

    @Override
    public void putShows(final Collection<ShowWrapper> shows) {
        mExecutor.execute(new DatabaseBackgroundRunnable<Void>() {
            @Override
            public Void doDatabaseCall(DatabaseHelper dbHelper) {
                dbHelper.deleteShows(shows);
                return null;
            }
        });
    }

    @Override
    public void put(final ShowWrapper show) {
        mExecutor.execute(new DatabaseBackgroundRunnable<Void>() {
            @Override
            public Void doDatabaseCall(DatabaseHelper dbHelper) {
                dbHelper.put(show);
                return null;
            }
        });
    }

    @Override
    public void deleteMovies(final Collection<MovieWrapper> movies) {
        mExecutor.execute(new DatabaseBackgroundRunnable<Void>() {
            @Override
            public Void doDatabaseCall(DatabaseHelper dbHelper) {
                dbHelper.deleteMovies(movies);
                return null;
            }
        });
    }

    @Override
    public void deleteShows(final Collection<ShowWrapper> shows) {
        mExecutor.execute(new DatabaseBackgroundRunnable<Void>() {
            @Override
            public Void doDatabaseCall(DatabaseHelper dbHelper) {
                dbHelper.deleteShows(shows);
                return null;
            }
        });
    }

    @Override
    public void close() {
        mDbHelper.close();
    }

    @Override
    public void deleteAllMovies() {
        mExecutor.execute(new DatabaseBackgroundRunnable<Void>() {
            @Override
            public Void doDatabaseCall(DatabaseHelper dbHelper) {
                dbHelper.deleteAllMovies();
                return null;
            }
        });
    }

    @Override
    public void deleteAllShows() {
        mExecutor.execute(new DatabaseBackgroundRunnable<Void>() {
            @Override
            public Void doDatabaseCall(DatabaseHelper dbHelper) {
                dbHelper.deleteAllShows();
                return null;
            }
        });
    }

    private abstract class DatabaseBackgroundRunnable<R> extends BackgroundCallRunnable<R> {

        @Override
        public final R runAsync() {
            final DatabaseHelper dbHelper = mDbHelper;

            if (dbHelper.isClosed()) {
                return null;
            }

            return doDatabaseCall(dbHelper);
        }

        public abstract R doDatabaseCall(DatabaseHelper dbHelper);

    }

    private static void merge(DatabaseHelper dbHelper,
                              List<MovieWrapper> movieDbItems,
                              List<MovieWrapper> movieNewItems,
                              List<ShowWrapper>  showDbItems,
                              List<ShowWrapper>  showNewItems) {
        if (!MoviesCollections.isEmpty(movieDbItems)) {
            Map<Long, MovieWrapper> dbItemsMap = new ArrayMap<>();
            for (MovieWrapper movie : movieDbItems) {
                dbItemsMap.put(movie.getDBId(), movie);
            }

            // Now lets remove the items from the mapAll, leaving only those
            // not in the watchlist
            for (MovieWrapper movie : movieNewItems) {
                dbItemsMap.remove(movie.getDBId());
            }

            // Anything left in the dbItemsMap needs removing from the db
            if (!dbItemsMap.isEmpty()) {
                dbHelper.deleteMovies(dbItemsMap.values());
            }
        }

        if (!MoviesCollections.isEmpty(showDbItems)) {
            Map<Long, ShowWrapper> dbItemsMap = new ArrayMap<>();
            for (ShowWrapper show : showDbItems) {
                dbItemsMap.put(show.getDbId(), show);
            }

            // Now lets remove the items from the mapAll, leaving only those
            // not in the watchlist
            for (ShowWrapper show : showNewItems) {
                dbItemsMap.remove(show.getDbId());
            }

            // Anything left in the dbItemsMap needs removing from the db
            if (!dbItemsMap.isEmpty()) {
                dbHelper.deleteShows(dbItemsMap.values());
            }
        }

        // Now persist the correct list
        dbHelper.putMovies(movieNewItems);
        dbHelper.putShows(showNewItems);
    }
}
