package com.roodie.materialmovies.mvp.presenters;

import com.google.common.base.Preconditions;
import com.roodie.materialmovies.mvp.views.BaseMovieListView;
import com.roodie.materialmovies.mvp.views.UiView;
import com.roodie.model.entities.ListItem;
import com.roodie.model.state.ApplicationState;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * Created by Roodie on 14.08.2015.
 */
abstract class BaseGridPresenter<R extends BaseMovieListView> extends BasePresenter {

    private final Set<R> mUis;
    private final Set<R> mUnmodifiableUis;

    protected static final int TMDB_FIRST_PAGE = 1;

    public Set<R> getmUnmodifiableUis() {
        return mUnmodifiableUis;
    }

    public BaseGridPresenter() {
        mUis = new CopyOnWriteArraySet<>();
        mUnmodifiableUis = Collections.unmodifiableSet(mUis);
    }


    protected abstract void onUiAttached(final R ui);

    public abstract void onScrolledToBottom(R ui);

    protected abstract void populateUi(final R ui);

    public abstract String getUiTitle(R ui);

    public synchronized final void attachUi(R view) {
        Preconditions.checkNotNull(view, "View cannot be null");
        Preconditions.checkState(!mUis.contains(view), "UI is already attached");
        mUis.add(view);
        onUiAttached(view);
        populateUi(view);
    }

    public synchronized final void detachUi(R view) {
        Preconditions.checkArgument(view != null, "ui cannot be null");
        Preconditions.checkState(mUis.contains(view), "ui is not attached");
        mUis.remove(view);
    }

    protected final Set<R> getUis() {
        return mUnmodifiableUis;
    }

    protected int getId(R view) {
        return view.hashCode();
    }

    protected synchronized R findUi(final int id) {
        for (R ui : mUis) {
            if (getId(ui) == id) {
                return ui;
            }
        }
        return null;
    }

    protected R findUiFromQueryType(UiView.MovieQueryType queryType) {
        for (R ui : getUis()) {
            if (ui.getQueryType() == queryType) {
                return ui;
            }
        }
        return null;
    }

    protected boolean canFetchNextPage(ApplicationState.PaginatedResult<?> paginatedResult) {
        return paginatedResult != null && paginatedResult.page < paginatedResult.totalPages;
    }

    protected  <T extends ListItem<T>> List<ListItem<T>> createListItemList(final List<T> items) {
        Preconditions.checkNotNull(items, "items cannot be null");
        ArrayList<ListItem<T>> listItems = new ArrayList<>(items.size());
        for (ListItem<T> item : items) {
            listItems.add(item);
        }
        return listItems;
    }

    protected final void populateUiFromQueryType(UiView.MovieQueryType queryType) {
        R ui = findUiFromQueryType(queryType);
        if (ui != null) {
            populateUi(ui);
        }
    }

}
