package com.roodie.materialmovies.views.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.common.base.Preconditions;
import com.roodie.materialmovies.R;
import com.roodie.materialmovies.mvp.presenters.MovieImagesPresenter;
import com.roodie.materialmovies.views.fragments.base.BaseFragment;
import com.roodie.model.entities.MovieWrapper;
import com.roodie.model.network.NetworkError;

import java.util.List;

/**
 * Created by Roodie on 27.06.2015.
 */
public class MovieImagesFragment extends BaseFragment implements  MovieImagesPresenter.MovieImagesView {

    private static final String MOVIE_ID = "movie_id";
    private static final String CURRENT_ITEM = "viewpager_current";

    private MovieImagesPresenter mPresenter;
    List<MovieWrapper.BackgroundImage> mImages;

    private ViewPager mViewPager;
    private ImageAdapter mAdapter;

    private int mVisibleItem;

    public static MovieImagesFragment newInstance(String movieId) {
        Preconditions.checkArgument(!TextUtils.isEmpty(movieId), "MovieId can not be null");

        Bundle bundle = new Bundle();
        bundle.putString(MOVIE_ID, movieId);
        MovieImagesFragment fragment = new MovieImagesFragment();
        fragment.setArguments(bundle);

        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_movie_images, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mAdapter = new ImageAdapter();

        mViewPager = (ViewPager) view.findViewById(R.id.viewpager);
        mViewPager.setAdapter(mAdapter);

        if (savedInstanceState != null && savedInstanceState.containsKey(CURRENT_ITEM)) {
            mVisibleItem = savedInstanceState.getInt(CURRENT_ITEM);
        }
    }

    @Override
    public void onPause() {
        mVisibleItem = mViewPager.getCurrentItem();
        super.onPause();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(CURRENT_ITEM, mViewPager.getCurrentItem());
        super.onSaveInstanceState(outState);
    }


    /**
     * MovieView
     */
    @Override
    public MovieQueryType getQueryType() {
        return MovieQueryType.MOVIE_IMAGES;
    }

    @Override
    public boolean isModal() {
        return false;
    }

    /**
     * MovieImagesView
     */
    @Override
    public void setItems(List<MovieWrapper.BackgroundImage> images) {
        mImages = images;
        if (mAdapter != null) {
            mAdapter.notifyDataSetChanged();
        }
    }


    @Override
    public String getRequestParameter() {
        return null;
    }

    @Override
    public void showSecondaryLoadingProgress(boolean visible) {

    }

    @Override
    public void showLoadingProgress(boolean visible) {

    }

    @Override
    public void showError(NetworkError error) {

    }

    private class ImageAdapter extends PagerAdapter {

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            return super.instantiateItem(container, position);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            super.destroyItem(container, position, object);
        }

        @Override
        public int getCount() {
            return 0;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return false;
        }
    }
}
