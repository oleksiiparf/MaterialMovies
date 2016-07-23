package com.roodie.materialmovies.views.fragments.base;

import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.google.common.base.Preconditions;
import com.marshalchen.ultimaterecyclerview.quickAdapter.easyRegularAdapter;
import com.roodie.materialmovies.R;
import com.roodie.materialmovies.mvp.views.ListTvShowsView;
import com.roodie.materialmovies.util.AnimUtils;
import com.roodie.materialmovies.util.MMoviesPreferences;
import com.roodie.materialmovies.views.activities.SettingsActivity;
import com.roodie.materialmovies.views.adapters.ShowsGridAdapter;
import com.roodie.materialmovies.views.custom_views.TvShowDialogView;
import com.roodie.materialmovies.views.custom_views.recyclerview.RecyclerInsetsDecoration;
import com.roodie.model.Display;
import com.roodie.model.entities.ShowWrapper;

import java.util.List;

/**
 * Created by Roodie on 15.08.2015.
 */
public abstract class TvShowsGridFragment extends BaseGridFragment<ShowsGridAdapter.ShowViewHolder, ShowWrapper> implements ListTvShowsView {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.fragment_show_recycler;
    }

    public MMoviesQueryType getQueryType() {
        return MMoviesQueryType.COMMON_SHOWS;
    }

    @Override
    protected easyRegularAdapter<ShowWrapper, ShowsGridAdapter.ShowViewHolder> createAdapter(List<ShowWrapper> data) {
        return new ShowsGridAdapter(data, getActivity(),getMvpDelegate(), this);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_refresh:{
                if (!hasAdapter()) {
                    onRefreshData(false);
                } else {
                    onRefreshData(true);
                }
                return true;
            }
        }
        return false;
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getRecyclerView().addItemDecoration(new RecyclerInsetsDecoration(getActivity(), NavigationGridType.SHOWS));

        //set actionbar up navigation
        final Display display = getDisplay();
        if (display != null) {
            display.showUpNavigation(getQueryType() != null && getQueryType().showUpNavigation());
        }

    }

    @Override
    public void updateDisplaySubtitle(String subtitle) {

    }

    @Override
    public void updateDisplayTitle(String title) {

    }


    @Override
    public void showItemDetail(ShowWrapper tvShow, View view) {
        Preconditions.checkNotNull(tvShow, "tv cannot be null");

        Display display = getDisplay();
        if (display != null) {
            if (tvShow.getTmdbId() != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && MMoviesPreferences.areAnimationsEnabled(getContext()) && view.getTag() != null) {
                    display.startTvDetailActivityBySharedElements(String.valueOf(tvShow.getTmdbId()), view, (String) view.getTag());
                } else {
                    display.startTvDetailActivity(String.valueOf(tvShow.getTmdbId()), null);
                }
            }
        }

    }

    @Override
    public void showContextMenu(final ShowWrapper tvShow) {
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
                            AnimUtils.hideImageCircular(dialogView, dialog);
                        }
                    }
                })
                .build();

        localMaterialDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            public void onShow(DialogInterface paramDialogInterface) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    AnimUtils.revealImageCircular(dialogView);
                    return;
                }
                dialogView.setVisibility(View.VISIBLE);
            }
        });
                localMaterialDialog.show();

        dialogView.setSummary(tvShow.getOverview());
        dialogView.getCoverImageView().loadPoster(tvShow);
        dialogView.setYear(String.valueOf(tvShow.getReleaseDate()));
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

    @Override
    public void onClick(View view, int position) {
            ShowWrapper item = mAdapter.getObjects().get(position);
            showItemDetail(item, view);
    }

    @Override
    public void onPopupMenuClick(View view, int position) {

    }
}
