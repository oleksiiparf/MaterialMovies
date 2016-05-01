package com.roodie.materialmovies.mvp.views;

import android.graphics.Bitmap;
import android.view.View;

import com.arellomobile.mvp.GenerateViewState;
import com.roodie.model.entities.PersonWrapper;
import com.roodie.model.entities.SeasonWrapper;
import com.roodie.model.entities.ShowWrapper;

import java.util.List;

/**
 * Created by Roodie on 02.07.2015.
 */

@GenerateViewState
public interface TvShowDetailView extends MvpLceView<ShowWrapper> {

    void setSeasons(List<SeasonWrapper> data);

    void showTvShowImages(ShowWrapper movie);

    void showTvShowCreditsDialog(MMoviesQueryType queryType);

    void showSeasonDetail(SeasonWrapper season);

    void updateDisplaySubtitle(CharSequence subtitle);

    void showPersonDetail(PersonWrapper person, View ui);

    void setSeasonPoster(Bitmap bitmap);

}
