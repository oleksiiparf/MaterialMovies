package com.roodie.materialmovies.views.fragments.base;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.google.common.base.Preconditions;
import com.roodie.materialmovies.R;
import com.roodie.materialmovies.mvp.presenters.ShowGridPresenter;
import com.roodie.materialmovies.util.AnimationUtils;
import com.roodie.materialmovies.views.MMoviesApplication;
import com.roodie.materialmovies.views.activities.SettingsActivity;
import com.roodie.materialmovies.views.adapters.ShowGridAdapter;
import com.roodie.materialmovies.views.custom_views.RecyclerInsetsDecoration;
import com.roodie.materialmovies.views.custom_views.TvShowDialogView;
import com.roodie.model.Display;
import com.roodie.model.entities.ListItem;
import com.roodie.model.entities.ShowWrapper;
import com.roodie.model.network.NetworkError;

import java.util.List;

/**
 * Created by Roodie on 15.08.2015.
 */
public abstract class ShowGridFragment extends BaseGridFragment implements ShowGridPresenter.ShowGridView {

    protected ShowGridPresenter mPresenter;
    private ShowGridAdapter mShowsAdapter;

    private static final String LOG_TAG = ShowGridFragment.class.getSimpleName();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.show_view_recycler, container, false);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mPresenter = MMoviesApplication.from(activity.getApplicationContext()).getShowGridPresenter();
        Log.d(LOG_TAG, "Presenter: " + mPresenter);
    }

    @Override
    public void onDetach() {
        super.onDetach();
       // mPresenter.detachUi(this);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_refresh:
                mPresenter.refresh(this);
                return true;
            case  R.id.menu_search:
                getDisplay().showSearchFragment();
                return true;
        }
        return false;
    }

    @Override
    public void setItems(List<ListItem<ShowWrapper>> listItems) {
         mShowsAdapter.setItems(listItems);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //set actionbar up navigation
        final Display display = getDisplay();
        if (!isModal()) {
            display.showUpNavigation(getQueryType() != null && getQueryType().showUpNavigation());
        }
        mPresenter.attachUi(this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mPresenter.detachUi(this);
    }

    @Override
    public void initializeRecycler() {
        getRecyclerView().addItemDecoration(new RecyclerInsetsDecoration(getActivity().getApplicationContext()));
        mShowsAdapter = new ShowGridAdapter(null);
        mShowsAdapter.setClickListener(this);
        getRecyclerView().setAdapter(mShowsAdapter);
        getRecyclerView().setOnScrollListener(recyclerScrollListener);
    }

    @Override
    protected boolean onScrolledToBottom() {
        if (hasPresenter()) {
            getPresenter().onScrolledToBottom(this);
            return true;
        }
        return false;
    }

    protected final boolean hasPresenter() {
        return mPresenter != null;
    }

    public ShowGridPresenter getPresenter() {
        return mPresenter;
    }

    @Override
    public void updateDisplayTitle(String title) {
    }

    @Override
    public void showError(NetworkError error) {
        setGridShown(true);

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
    public String getTitle() {
        if (hasPresenter()) {
            return getPresenter().getUiTitle(this);
        }
        return null;
    }

    @Override
    public void showLoadingProgress(boolean visible) {
        if (visible) {
            setGridShown(false);
        } else {
            setGridShown(true);
        }
    }

    @Override
    public void showSecondaryLoadingProgress(boolean visible) {
        setSecondaryProgressShown(visible);
    }

    @Override
    public String getRequestParameter() {
        return null;
    }

    @Override
    public void showTvDetail(ShowWrapper tvShow) {
        Preconditions.checkNotNull(tvShow, "tv cannot be null");
        //int[] startingLocation = new int[2];
       // view.getLocationOnScreen(startingLocation);
        //startingLocation[0] += view.getWidth() / 2;
        //startingLocation[1] += view.getHeight() / 2;

        Display display = getDisplay();
        if (display != null) {
            if (tvShow.getTmdbId() != null) {
                display.startTvDetailActivity(String.valueOf(tvShow.getTmdbId()), null);
                //if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                //    System.out.println("Start by shared element");
                //    display.startMovieDetailActivityBySharedElements(String.valueOf(movie.getTmdbId()), view, (String) view.getTag());
                //} else {
                //    System.out.println("Start by animation");
                //    display.startMovieDetailActivityByAnimation(String.valueOf(movie.getTmdbId()), startingLocation);
                //}
            }
        }

    }

    @Override
    public void showTvShowDialog(final ShowWrapper tvShow) {

       // View localView = View.inflate(getActivity())
        final TvShowDialogView dialogView = new TvShowDialogView(getActivity());


        MaterialDialog localMaterialDialog = new MaterialDialog.Builder(getActivity())
                .title(tvShow.getTitle())
                .autoDismiss(false)
                .customView(dialogView, true)
                .theme(SettingsActivity.THEME == R.style.Theme_MMovies_Light ? Theme.LIGHT : Theme.DARK)
                .negativeText(getActivity().getString(R.string.close_dialog_window)).callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onNegative(MaterialDialog dialog) {
                        super.onNegative(dialog);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            AnimationUtils.hideImageCircular(dialogView, dialog);
                        }
                    }
                })
                .build();

        localMaterialDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            public void onShow(DialogInterface paramDialogInterface) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    AnimationUtils.revealImageCircular(dialogView);
                    return;
                }
                dialogView.setVisibility(View.VISIBLE);
            }
        });
                localMaterialDialog.show();

        dialogView.setSummary(tvShow.getOverview());
        dialogView.getCoverImageView().loadPoster(tvShow);
        dialogView.setYear(String.valueOf(tvShow.getFirstAirDate()));
        dialogView.setRating(tvShow.getAverageRatingPercent() + "%");
        dialogView.getLikeButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        dialogView.getShareButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getDisplay() != null) {
                    getDisplay().shareTvShow(tvShow.getTmdbId(), tvShow.getTitle());
                }
            }
        });


    }

    private RecyclerView.OnScrollListener recyclerScrollListener = new RecyclerView.OnScrollListener() {

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {

            super.onScrolled(recyclerView, dx, dy);

            int visibleItemCount    = getRecyclerView().getLayoutManager().getChildCount();
            int totalItemCount      = getRecyclerView().getLayoutManager().getItemCount();
            int pastVisibleItems    = ((GridLayoutManager) getRecyclerView().getLayoutManager())
                    .findFirstVisibleItemPosition();

            if((visibleItemCount + pastVisibleItems) >= totalItemCount) {
                onScrolledToBottom();
            }
        }
    };

    @Override
    public void onClick(View view, int position) {
        if (hasPresenter()) {

            ListItem<ShowWrapper> item = mShowsAdapter.getItem(position);
            // Log.d(LOG_TAG, "List item clicked  " + item.getListItem().getTitle());
            Log.d(LOG_TAG, getQueryType() + " clicked");
            if (item.getListType() == ListItem.TYPE_ITEM) {
                showTvDetail(item.getListItem());
            }
        }
    }

    @Override
    public void onPopupMenuClick(View view, int position) {

    }
}
