package com.roodie.model.repository;

import android.content.ContentValues;
import android.database.Cursor;

import com.roodie.model.entities.MovieWrapper;
import com.roodie.model.sqlite.Column;
import com.roodie.model.sqlite.SQLiteHelper;

/**
 * Created by Roodie on 11.03.2016.
 */
public class MovieRepository extends SQLiteRepository<MovieWrapper> {

    public MovieRepository(SQLiteHelper dbHelper) {
        super(dbHelper);
    }

    @Override
    protected String getTableName() {
        return "Movie";
    }

    @Override
    protected Column[] getColumns() {
        return MovieColumns.values();
    }

    @Override
    protected MovieWrapper createObjectFromCursor(Cursor cursor) {
        String dbId = MovieColumns.ID.readValue(cursor);
        MovieWrapper movie = new MovieWrapper(dbId);
        movie.setTitle((String) MovieColumns.TITLE.readValue(cursor));
        movie.setTmdbBackdropUrl((String) MovieColumns.BACKDROP_PATH.readValue(cursor));
        movie.setTmdbPosterUrl((String) MovieColumns.POSTER_PATH.readValue(cursor));
        movie.setOverview((String) MovieColumns.OVERVIEW.readValue(cursor));
        movie.setReleasedTime((Long) MovieColumns.RELEASE_DATE.readValue(cursor));
        movie.setTmdbRatingVotesAmount((Integer) MovieColumns.VOTE_COUNT.readValue(cursor));
        movie.setTmdbRatingVotesAverage((Double) MovieColumns.VOTE_AVERAGE.readValue(cursor));
        movie.setWatched((Boolean) MovieColumns.IS_WATCHED.readValue(cursor));
        return movie;
    }

    @Override
    protected ContentValues createContentValuesFromObject(MovieWrapper item) {
        ContentValues movieValue = new ContentValues();
        MovieColumns.ID.addValue(movieValue, String.valueOf(item.getTmdbId()));
        MovieColumns.TITLE.addValue(movieValue, item.getTitle());
        MovieColumns.BACKDROP_PATH.addValue(movieValue, item.getBackdropUrl());
        MovieColumns.POSTER_PATH.addValue(movieValue, item.getPosterUrl());
        MovieColumns.OVERVIEW.addValue(movieValue, item.getOverview());
        MovieColumns.RELEASE_DATE.addValue(movieValue, item.getReleasedTime());
        MovieColumns.VOTE_AVERAGE.addValue(movieValue, item.getVotesAverage());
        MovieColumns.VOTE_COUNT.addValue(movieValue, item.getRatingVotes());
        MovieColumns.IS_WATCHED.addValue(movieValue, item.isWatched());
        return movieValue;
    }
}
