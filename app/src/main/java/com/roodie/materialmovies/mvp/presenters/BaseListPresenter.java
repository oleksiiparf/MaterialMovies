package com.roodie.materialmovies.mvp.presenters;

import com.google.common.base.Preconditions;
import com.roodie.materialmovies.mvp.views.BaseMovieListView;
import com.roodie.materialmovies.mvp.views.UiView;
import com.roodie.materialmovies.qualifiers.GeneralPurpose;
import com.roodie.model.entities.ListItem;
import com.roodie.model.state.ApplicationState;
import com.roodie.model.tasks.BaseMovieRunnable;
import com.roodie.model.util.BackgroundExecutor;
import com.roodie.model.util.Injector;
import com.roodie.model.util.StringFetcher;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import javax.inject.Inject;

/**
 * Created by Roodie on 14.08.2015.
 */
class BaseListPresenter<R extends BaseMovieListView> extends BasePresenter<R> {

    private final Set<R> mUis;
    private final Set<R> mUnmodifiableUis;

    protected final BackgroundExecutor mExecutor;
    protected final Injector mInjector;
    protected final StringFetcher mStringFetcher;

    protected static final int TMDB_FIRST_PAGE = 1;

    public Set<R> getmUnmodifiableUis() {
        return mUnmodifiableUis;
    }



    @Inject
    public BaseListPresenter(ApplicationState moviesState,
                              @GeneralPurpose BackgroundExecutor executor,
                              Injector injector,
                              StringFetcher stringFetcher) {
        super(moviesState);
        mUis = new CopyOnWriteArraySet<>();
        mUnmodifiableUis = Collections.unmodifiableSet(mUis);
        mExecutor = Preconditions.checkNotNull(executor, "executor cannot be null");
        mInjector = Preconditions.checkNotNull(injector, "injector cannot be null");
        mStringFetcher = Preconditions.checkNotNull(stringFetcher, "stringFetcher cannot be null");

    }

    @Override
    public void initialize() {

    }

    protected  void onUiAttached(final R ui) {}

    public  void onScrolledToBottom(R ui) {}

    protected  void populateUi(final R ui) {}

    public  String getUiTitle(R ui) {
        return null;
    }

    public synchronized final void attachUi(R view) {
        attachView(view);
        Preconditions.checkState(!mUis.contains(view), "UI is already attached");
        mUis.add(view);
        onUiAttached(view);
        populateUi(view);
    }

    public synchronized final void detachUi(R view) {
        Preconditions.checkArgument(view != null, "ui cannot be null");
        detachView(true);
        //Preconditions.checkState(mUis.contains(view), "ui is not attached");
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

    protected  <M> void executeTask(BaseMovieRunnable<M> task) {
        mInjector.inject(task);
        mExecutor.execute(task);
    }


}
