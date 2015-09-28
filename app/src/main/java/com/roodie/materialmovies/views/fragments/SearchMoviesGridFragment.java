package com.roodie.materialmovies.views.fragments;

import android.os.Build;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.PopupMenu;

import com.google.common.base.Preconditions;
import com.roodie.materialmovies.R;
import com.roodie.materialmovies.views.adapters.SearchMovieGridAdapter;
import com.roodie.materialmovies.views.fragments.base.SearchGridFragment;
import com.roodie.materialmovies.views.listeners.MovieMenuItemClickListener;
import com.roodie.model.Display;
import com.roodie.model.entities.ListItem;
import com.roodie.model.entities.MovieWrapper;
import com.roodie.model.util.MoviesCollections;

import java.util.List;


/**
 * Created by Roodie on 22.08.2015.
 */

public class SearchMoviesGridFragment extends SearchGridFragment<MovieWrapper, SearchMovieGridAdapter> {


    @Override
    public void showMovieDetail(MovieWrapper movie, View view) {
        Preconditions.checkNotNull(movie, "movie cannot be null");
        int[] startingLocation = new int[2];
        view.getLocationOnScreen(startingLocation);
        startingLocation[0] += view.getWidth() / 2;
        startingLocation[1] += view.getHeight() / 2;

        Display display = getDisplay();
        if (display != null) {
            if (movie.getTmdbId() != null) {
                 if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    System.out.println("Start by shared element");
                    display.startMovieDetailActivityBySharedElements(String.valueOf(movie.getTmdbId()), view, (String) view.getTag());
                 } else {
                System.out.println("Start by animation");
                display.startMovieDetailActivityByAnimation(String.valueOf(movie.getTmdbId()), startingLocation);
                 }
            }
        }
    }

    @Override
    public MovieQueryType getQueryType() {
        return MovieQueryType.SEARCH_MOVIES;
    }

    @Override
    public void onClick(View view, int position) {
        if (hasPresenter()) {

            ListItem<MovieWrapper> item = getAdapter().getItem(position);
            Log.d(LOG_TAG, getQueryType() + " clicked");
            if (item.getListType() == ListItem.TYPE_ITEM) {
                showMovieDetail(item.getListItem(), view);
            }
        }
    }

    @Override
    public void onPopupMenuClick(View view, int position) {

        super.onPopupMenuClick(view, position);
        PopupMenu popupMenu = new PopupMenu(view.getContext(), view);
        popupMenu.inflate(R.menu.movie_popup_menu);

        MovieWrapper movieWrapper = getAdapter().getItem(position).getListItem();

        // show/hide some menu items depending on movie information
        Menu menu = popupMenu.getMenu();

        menu.findItem(R.id.menu_action_web_search)
                .setVisible(movieWrapper.getTitle() != null);

        menu.findItem(R.id.menu_action_trailer)
                .setVisible(movieWrapper != null && !MoviesCollections.isEmpty(movieWrapper.getTrailers()));

        popupMenu.setOnMenuItemClickListener(
                new MovieMenuItemClickListener(movieWrapper,getDisplay()));
        popupMenu.show();
    }

    @Override
    public void initializeRecycler() {
        super.initializeRecycler();
        getAdapter().setClickListener(this);
    }



    @Override
    protected SearchMovieGridAdapter createAdapter() {
        Log.d(LOG_TAG, "create Adapter");
        return new SearchMovieGridAdapter(null);
    }

    @Override
    public void setItems(List<ListItem<MovieWrapper>> listItems) {
        Log.d(LOG_TAG, "Set items to adapter");
        getAdapter().setItems(listItems);
    }
}
