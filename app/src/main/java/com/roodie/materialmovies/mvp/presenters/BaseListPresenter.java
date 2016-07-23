package com.roodie.materialmovies.mvp.presenters;

import com.roodie.materialmovies.mvp.views.BaseListView;
import com.roodie.materialmovies.mvp.views.UiView;
import com.roodie.model.state.ApplicationState;
import com.roodie.model.state.MoviesState;

/**
 * Created by Roodie on 14.02.2016.
 */
public interface BaseListPresenter<M extends BaseListView> extends BasePresenter<M> {

    int TMDB_FIRST_PAGE = 1;

    void onUiAttached(M view, UiView.MMoviesQueryType queryType, String parameter);

    String getUiTitle(UiView.MMoviesQueryType queryType);

    String getUiSubtitle(UiView.MMoviesQueryType queryType);

    void populateUi(M view, UiView.MMoviesQueryType queryType);

    boolean canFetchNextPage(ApplicationState.PaginatedResult<?> paginatedResult);

    void refresh(M view, UiView.MMoviesQueryType queryType);

    void onScrolledToBottom(M view, UiView.MMoviesQueryType queryType);

    void populateUiFromEvent(MoviesState.BaseEvent event, UiView.MMoviesQueryType queryType);

    M findUi(final int id);

}
