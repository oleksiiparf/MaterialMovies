package com.roodie.materialmovies.views.activities;

import android.content.Intent;
import android.os.Bundle;

import com.roodie.materialmovies.R;
import com.roodie.model.Display;

/**
 * Created by Roodie on 12.05.2016.
 */
public class SearchItemsActivity extends BaseNavigationActivity {

    public static final String MOVIE_TEMS = "_search_movie_items";
    public static final String SHOW_TEMS = "_search_show_items";
    public static final String PERSON_TEMS = "_search_person_items";
    @Override
    protected void handleIntent(Intent intent, Display display) {
        if (!display.hasMainFragment()) {
            String parameter = intent.getStringExtra(Display.PARAM_ID);
            switch (parameter) {
                case MOVIE_TEMS:
                    getDisplay().showSearchMoviesFragment();
                    break;
                case SHOW_TEMS:
                    getDisplay().showSearchTvShowsFragment();
                    break;
                case PERSON_TEMS:
                    getDisplay().showSearchPeopleFragment();
                    break;
            }
        }
    }

    @Override
    protected int getContentViewLayoutId() {
        return R.layout.activity_no_drawer;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        animateManually(false);
    }
}
