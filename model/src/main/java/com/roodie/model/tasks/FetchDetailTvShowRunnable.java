package com.roodie.model.tasks;

import com.roodie.model.entities.ShowWrapper;
import com.roodie.model.state.MoviesState;
import com.roodie.model.util.MoviesCollections;
import com.uwetrottmann.tmdb.entities.AppendToResponse;
import com.uwetrottmann.tmdb.entities.TvShowComplete;
import com.uwetrottmann.tmdb.enumerations.AppendToResponseItem;

import retrofit.RetrofitError;

/**
 * Created by Roodie on 16.09.2015.
 */
public class FetchDetailTvShowRunnable  extends BaseRunnable<TvShowComplete> {

    private final int mId;

    public FetchDetailTvShowRunnable(int callingId, int mId) {
        super(callingId);
        this.mId = mId;
    }

    @Override
    public TvShowComplete doBackgroundCall() throws RetrofitError {
        return getTmdbClient().tvService().summary(mId,
                getCountryProvider().getTwoLetterLanguageCode(),
                new AppendToResponse(
                        AppendToResponseItem.CREDITS,
                        AppendToResponseItem.VIDEOS,
                        AppendToResponseItem.CONTENT_RATINGS
                )
        );
    }

    @Override
    public void onSuccess(TvShowComplete result) {

        ShowWrapper show = getEntityMapper().map(result);
        show.markFullFetchCompleted();

        if (result.credits != null && !MoviesCollections.isEmpty(result.credits.cast)){
            show.setCast(getEntityMapper().mapCastCredits(result.credits.cast));
        }

        if (result.credits != null && !MoviesCollections.isEmpty(result.credits.crew)){
            show.setCrew(getEntityMapper().mapCrewCredits(result.credits.crew));
        }

        if (result.content_ratings != null) {
            show.updateContentRating(result.content_ratings,
                    getCountryProvider().getTwoLetterCountryCode());
        }

        if (!MoviesCollections.isEmpty(result.seasons)){
            show.setSeasons(getEntityMapper().mapTvSeasons(result.id, result.seasons));
        }


        //show.setWatched(true);
        //show.save();
        //getDbHelper().put(show);
        System.out.println(show.toString());
        getEventBus().post(new MoviesState.TvShowInformationUpdatedEvent(getCallingId(), show));
    }

    @Override
    public void onError(RetrofitError re) {
        if (re.getResponse() != null && re.getResponse().getStatus() == 404) {
            ShowWrapper show = mState.getTvShow(mId);
            if (show != null) {
                //getDbHelper().put(show);
                getEventBus().post(new MoviesState.TvShowInformationUpdatedEvent(getCallingId(), show));
            }
        }
        super.onError(re);
    }
}
