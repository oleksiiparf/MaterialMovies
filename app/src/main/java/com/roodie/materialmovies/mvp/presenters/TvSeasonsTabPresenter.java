package com.roodie.materialmovies.mvp.presenters;

import com.google.common.base.Preconditions;
import com.roodie.materialmovies.R;
import com.roodie.materialmovies.mvp.views.MovieView;
import com.roodie.model.entities.SeasonWrapper;
import com.roodie.model.entities.ShowWrapper;
import com.roodie.model.state.ApplicationState;
import com.roodie.model.util.StringFetcher;

import java.util.ArrayList;

import javax.inject.Inject;


/**
 * Created by Roodie on 02.08.2015.
 */
public class TvSeasonsTabPresenter extends BasePresenter<TvSeasonsTabPresenter.TvSeasonsTabView> {

    private final StringFetcher mStringFetcher;


    @Inject
    public TvSeasonsTabPresenter(ApplicationState state, StringFetcher mStringFetcher) {
        super(state);
        this.mStringFetcher = Preconditions.checkNotNull(mStringFetcher, "stringFetcher cannot be null");
    }

    @Override
    public void attachView(TvSeasonsTabView view) {
        super.attachView(view);
         if (!getView().isModal()) {
             getView().updateDisplayTitle(mStringFetcher.getString(R.string.seasons_title));
         }
    }

    @Override
    public void initialize() {
        populateTvSeasonsTabs(getView().getRequestParameter());
    }

    private void populateTvSeasonsTabs(String tvShowId) {
        ShowWrapper tvShow = mState.getTvShow(tvShowId);
        if (tvShow != null) {
            getView().setupTabs(new ArrayList<SeasonWrapper>(tvShow.getSeasons()));
        }

    }

    public interface TvSeasonsTabView extends MovieView {
        void setupTabs(ArrayList<SeasonWrapper> list);
    }
}