package com.roodie.materialmovies.views.fragments;

import android.content.DialogInterface;
import android.os.Build;
import android.util.Log;
import android.view.View;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.roodie.materialmovies.R;
import com.roodie.materialmovies.util.AnimationUtils;
import com.roodie.materialmovies.views.activities.SettingsActivity;
import com.roodie.materialmovies.views.adapters.SearchShowGridAdapter;
import com.roodie.materialmovies.views.custom_views.TvShowDialogView;
import com.roodie.materialmovies.views.fragments.base.SearchGridFragment;
import com.roodie.model.entities.ListItem;
import com.roodie.model.entities.ShowWrapper;

import java.util.List;

/**
 * Created by Roodie on 22.08.2015.
 */
public class SearchTvShowsGridFragment extends SearchGridFragment<ShowWrapper, SearchShowGridAdapter> {

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

    @Override
    public MovieQueryType getQueryType() {
        return MovieQueryType.SEARCH_SHOWS;
    }

    @Override
    public void onClick(View view, int position) {
        if (hasPresenter()) {

            ListItem<ShowWrapper> item = getAdapter().getItem(position);
            Log.d(LOG_TAG, getQueryType() + " clicked");
            if (item.getListType() == ListItem.TYPE_ITEM) {
                showTvShowDialog(item.getListItem());
            }
        }
    }

    @Override
    public void initializeRecycler() {
        super.initializeRecycler();
        getAdapter().setClickListener(this);
    }

    @Override
    protected SearchShowGridAdapter createAdapter() {
        return new SearchShowGridAdapter(null);
    }

    @Override
    public void setItems(List<ListItem<ShowWrapper>> listItems) {
        getAdapter().setItems(listItems);
    }
}
