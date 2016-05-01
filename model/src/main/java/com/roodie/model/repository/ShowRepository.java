package com.roodie.model.repository;

import android.content.ContentValues;
import android.database.Cursor;

import com.roodie.model.entities.ShowWrapper;
import com.roodie.model.sqlite.Column;
import com.roodie.model.sqlite.SQLiteHelper;

/**
 * Created by Roodie on 13.03.2016.
 */
public class ShowRepository extends SQLiteRepository<ShowWrapper> {

    public ShowRepository(SQLiteHelper dbHelper) {
        super(dbHelper);
    }

    @Override
    protected String getTableName() {
        return "TvShow";
    }

    @Override
    protected Column[] getColumns() {
        return MovieColumns.values();
    }

    @Override
    protected ShowWrapper createObjectFromCursor(Cursor cursor) {
        String dbId = ShowColumns.ID.readValue(cursor);
        ShowWrapper show = new ShowWrapper(dbId);
        show.setTitle((String) ShowColumns.TITLE.readValue(cursor));
        show.setTmdbBackdropUrl((String) ShowColumns.BACKDROP_PATH.readValue(cursor));
        show.setTmdbPosterUrl((String) ShowColumns.POSTER_PATH.readValue(cursor));
        show.setOverview((String) ShowColumns.OVERVIEW.readValue(cursor));
        show.setReleasedTime((Long) ShowColumns.RELEASE_DATE.readValue(cursor));
        show.setTmdbRatingVotesAmount((Integer) ShowColumns.VOTE_COUNT.readValue(cursor));
        show.setTmdbRatingVotesAverage((Double) ShowColumns.VOTE_AVERAGE.readValue(cursor));
        show.setWatched((Boolean) ShowColumns.IS_WATCHED.readValue(cursor));
        return show;
    }

    @Override
    protected ContentValues createContentValuesFromObject(ShowWrapper item) {
        ContentValues showValue = new ContentValues();
        ShowColumns.ID.addValue(showValue, String.valueOf(item.getTmdbId()));
        ShowColumns.TITLE.addValue(showValue, item.getTitle());
        ShowColumns.BACKDROP_PATH.addValue(showValue, item.getBackdropUrl());
        ShowColumns.POSTER_PATH.addValue(showValue, item.getPosterUrl());
        ShowColumns.OVERVIEW.addValue(showValue, item.getOverview());
        ShowColumns.RELEASE_DATE.addValue(showValue, item.getReleasedTime());
        ShowColumns.VOTE_AVERAGE.addValue(showValue, item.getVotesAverage());
        ShowColumns.VOTE_COUNT.addValue(showValue, item.getRatingVotes());
        ShowColumns.IS_WATCHED.addValue(showValue, item.isWatched());
        return showValue;
    }
}
