package com.roodie.materialmovies.views.fragments.base;

/**
 * Created by Roodie on 07.09.2015.
 */
public abstract class BaseSearchListFragment { /*<M extends Watchable> extends OldListFragment<ListView> implements SearchPresenter.SearchView<M> {

    private SearchPresenter mPresenter;

    private boolean hasPresenter() {
        return mPresenter != null;
    }

    public SearchPresenter getPresenter() {
        return mPresenter;
    }

    @Override
    public boolean hasToolbar() {
        return false;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mPresenter = MMoviesApp.from(activity.getApplicationContext()).getSearchPresenter();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public ListView createListView(Context context, LayoutInflater inflater) {
        return (ListView) inflater.inflate(com.roodie.materialmovies.R.layout.view_pinned_list, null);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mPresenter.attachView(this);
        mPresenter.initialize();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mPresenter.detachView(true);
    }

    @Override
    public void onResume() {
        mPresenter.onResume();
        super.onResume();
    }

    @Override
    public void onPause() {
        mPresenter.onPause();
        super.onPause();
    }

    @Override
    protected boolean onScrolledToBottom() {
        return false;
    }

    @Override
    public boolean isModal() {
        return false;
    }


    @Override
    public void updateDisplayTitle(String title) {

    }

    @Override
    public void updateDisplaySubtitle(String subtitle) {

    }

    @Override
    public String getRequestParameter() {
        return null;
    }

    @Override
    public void showSecondaryLoadingProgress(boolean visible) {
        setSecondaryProgressShown(visible);
    }

    @Override
    public void showLoadingProgress(boolean visible) {
        if (visible) {
            setListShown(false);
        } else {
            setListShown(true);
        }
    }

    @Override
    public void showError(NetworkError error) {
        setListShown(pulltorefresh);

        switch (error) {
            case NETWORK_ERROR:
                setEmptyText(getString(R.string.empty_network_error, getTitle()));
                break;
            case UNKNOWN:
                setEmptyText(getString(R.string.empty_unknown_error, getTitle()));
                break;
        }
    }

    @Override
    public String getSubtitle() {
        if (hasPresenter()) {
            return getPresenter().getUiSubTitle();
        }
        return null;
    }

    @Override
    public String getTitle() {
        if (hasPresenter()) {
            return getPresenter().getUiTitle();
        }
        return null;
    }

    @Override
    public void onUiAttached() {
        Display display = getDisplay();
        if (display != null) {
            display.setActionBarTitle(getTitle());
            display.setActionBarSubtitle(getSubtitle());
        }

    }

    @Override
    public void showTvShowDialog(ShowWrapper tvShow) {
        //TODO
    }

    @Override
    public void showPersonDetail(PersonWrapper person, View view) {
        //TODO
    }

    @Override
    public void showMovieDetail(MovieWrapper movie, View view) {
        //TODO
    }

    @Override
    public void showTvShowDetail(ShowWrapper show, View view) {
        //TODO
    }

    @Override
    public void setSearchResult(MoviesState.SearchResult result) {
        //TODO
    }
*/}
