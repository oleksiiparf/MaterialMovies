package com.roodie.materialmovies.views.fragments;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.google.common.base.Preconditions;
import com.roodie.materialmovies.R;
import com.roodie.materialmovies.mvp.presenters.MovieImagesPresenter;
import com.roodie.materialmovies.views.MMoviesApplication;
import com.roodie.materialmovies.views.custom_views.MMoviesImageView;
import com.roodie.materialmovies.views.fragments.base.BaseFragment;
import com.roodie.model.Display;
import com.roodie.model.entities.MovieWrapper;
import com.roodie.model.network.NetworkError;

import java.util.List;

import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * Created by Roodie on 27.06.2015.
 */
public class MovieImagesFragment extends BaseFragment implements  MovieImagesPresenter.MovieImagesView {

    private static final String MOVIE_ID = "movie_id";
    private static final String CURRENT_ITEM = "viewpager_current";

    private MovieImagesPresenter mPresenter;
    List<MovieWrapper.BackdropImage> mImages;

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

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mPresenter = MMoviesApplication.from(activity.getApplicationContext()).getMovieImagesPresenter();
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
        mViewPager.setPageTransformer(true, new ZoomOutPageTransformer());
        mViewPager.setOffscreenPageLimit(2);
        mViewPager.setAdapter(mAdapter);

        if (savedInstanceState != null && savedInstanceState.containsKey(CURRENT_ITEM)) {
            mVisibleItem = savedInstanceState.getInt(CURRENT_ITEM);
        }
        mPresenter.attachView(this);
        mPresenter.initialize();
    }

    @Override
    public void onPause() {
        mVisibleItem = mViewPager.getCurrentItem();
        mPresenter.onPause();
        super.onPause();
    }

    @Override
    public void onResume() {
        mPresenter.onResume();
        super.onResume();
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
    public void setItems(List<MovieWrapper.BackdropImage> images) {
        mImages = images;
        if (mAdapter != null) {
            System.out.println("Adapter != null");
            mAdapter.notifyDataSetChanged();
            mViewPager.setCurrentItem(mVisibleItem);
        }
    }


    @Override
    public String getRequestParameter() {
        return getArguments().getString(MOVIE_ID);
    }

    @Override
    public void updateDisplayTitle(String title) {
        Display display = getDisplay();
        if (display != null) {
            display.setActionBarTitle(title);
        }
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
            final MovieWrapper.BackdropImage image = mImages.get(position);

            final View view = getLayoutInflater(null).inflate(R.layout.item_movie_image, container, false);

            final ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.progress_bar);

            MMoviesImageView imageView = (MMoviesImageView) view.findViewById(R.id.imageview_backdrop);

            final PhotoViewAttacher attacher = new PhotoViewAttacher(imageView);

            imageView.loadBackdrop(image, new MMoviesImageView.OnLoadedListener() {
                @Override
                public void onSuccess(MMoviesImageView imageView, Bitmap bitmap) {
                    progressBar.setVisibility(View.GONE);
                    attacher.update();
                }

                @Override
                public void onError(MMoviesImageView imageView) {
                    progressBar.setVisibility(View.GONE);
                    attacher.update();
                }
            });

            container.addView(view, ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT);

            return new PagerItem(view, attacher);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            final PagerItem pagerItem = (PagerItem) object;
            pagerItem.photoViewAttacher.cleanup();
            container.removeView(pagerItem.view);
            pagerItem.clear();
        }

        @Override
        public int getCount() {
            return mImages != null ? mImages.size() : 0;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == ((PagerItem) object).view;
        }
    }

    private static class PagerItem {
        View view;
        PhotoViewAttacher photoViewAttacher;

        PagerItem(View view, PhotoViewAttacher photoViewAttacher) {
            this.view = view;
            this.photoViewAttacher = photoViewAttacher;
        }

        void clear() {
            view = null;
            photoViewAttacher = null;
        }
    }

    public class ZoomOutPageTransformer implements ViewPager.PageTransformer {
        private static final float MIN_SCALE = 0.85f;
        private static final float MIN_ALPHA = 0.5f;

        public void transformPage(View view, float position) {
            int pageWidth = view.getWidth();
            int pageHeight = view.getHeight();

            if (position < -1) { // [-Infinity,-1)
                // This page is way off-screen to the left.
                view.setAlpha(0);

            } else if (position <= 1) { // [-1,1]
                // Modify the default slide transition to shrink the page as well
                float scaleFactor = Math.max(MIN_SCALE, 1 - Math.abs(position));
                float vertMargin = pageHeight * (1 - scaleFactor) / 2;
                float horzMargin = pageWidth * (1 - scaleFactor) / 2;
                if (position < 0) {
                    view.setTranslationX(horzMargin - vertMargin / 2);
                } else {
                    view.setTranslationX(-horzMargin + vertMargin / 2);
                }

                // Scale the page down (between MIN_SCALE and 1)
                view.setScaleX(scaleFactor);
                view.setScaleY(scaleFactor);

                // Fade the page relative to its size.
                view.setAlpha(MIN_ALPHA +
                        (scaleFactor - MIN_SCALE) /
                                (1 - MIN_SCALE) * (1 - MIN_ALPHA));

            } else { // (1,+Infinity]
                // This page is way off-screen to the right.
                view.setAlpha(0);
            }
        }
    }
}
