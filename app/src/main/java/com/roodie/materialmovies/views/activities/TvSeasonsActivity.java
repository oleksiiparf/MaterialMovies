package com.roodie.materialmovies.views.activities;

import android.animation.LayoutTransition;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;

import com.roodie.materialmovies.R;
import com.roodie.materialmovies.views.fragments.TvSeasonsFragment;
import com.roodie.model.Display;
import com.roodie.model.entities.SeasonWrapper;

import java.util.ArrayList;

import butterknife.Bind;

/**
 * Created by Roodie on 23.09.2015.
 */
public class TvSeasonsActivity extends BaseNavigationActivity {

    public static final String FRAGMENT_TAG_DETAILS = "detailsFragmentTag";
    public static final String FRAGMENT_TAG_SEASONS = "seasonsFragmentTag";

    private TvSeasonsFragment mListFragment;
    private Fragment mDetailsragment;

    @Bind(R.id.fragment_main)  ViewGroup leftPane;
    @Bind(R.id.fragment_detail) ViewGroup rightPane;
    @Nullable @Bind(R.id.paneContainer) ViewGroup paneContainer;

    private ViewPager mPager;
    private TabLayout mTabs;

    private boolean mDualPane;

    private ArrayList<SeasonWrapper> mSeasons;

    private int mSeasonId;

    private int mSeasonNumber;

    private int mShowId;

    public interface InitBundle {

        String SHOW_ID = "_show_id";

        String SEASON_ID = "_season_id";

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seasons);



        mDetailsragment = findDetailsFragment();

        if (savedInstanceState == null) {
            mListFragment = TvSeasonsFragment.newInstance(0, 0, 0, 0);
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.add(R.id.fragment_main, mListFragment, FRAGMENT_TAG_SEASONS).commit();
        } else {
            mListFragment = (TvSeasonsFragment) getSupportFragmentManager().findFragmentByTag(
                    FRAGMENT_TAG_SEASONS);
        }

        if (mDetailsragment != null) {
            rightPane.setVisibility(View.VISIBLE);
        }


        if (paneContainer != null) {
            //EnableAnimation
            LayoutTransition transition = new LayoutTransition();
            //transition.enableTransitionType(LayoutTransition.CHANGING);
            paneContainer.setLayoutTransition(transition);
        }

        /*
        // check for dual pane layout
        final ViewPager pager = (ViewPager) findViewById(R.id.pager_seasons);
        mDualPane = pager != null && pager.getVisibility() == View.VISIBLE;

        mTabs  = (TabLayout) findViewById(R.id.tabs);
        mTabs.post(new Runnable() {
            @Override
            public void run() {
                mTabs.setupWithViewPager(pager);
            }
        });
        */

        boolean isFinishing = false;
    }


    @Override
    protected void handleIntent(Intent intent, Display display) {

    }

    private Fragment findDetailsFragment() {
        return getSupportFragmentManager().findFragmentByTag(FRAGMENT_TAG_DETAILS);
    }

    /**
     * @return true if a fragment has been removed, otherwise false
     */
    private boolean removeDetailsFragment() {
        Fragment detailsFragment = findDetailsFragment();
        if (detailsFragment != null) {
            rightPane.setVisibility(View.GONE);
            getSupportFragmentManager().beginTransaction().remove(detailsFragment).commit();
            return true;
        }

        return false;
    }



}
