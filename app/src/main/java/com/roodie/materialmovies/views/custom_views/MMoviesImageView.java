package com.roodie.materialmovies.views.custom_views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.roodie.materialmovies.R;
import com.roodie.materialmovies.drawable.RoundedDrawable;
import com.roodie.materialmovies.util.AnimUtils;
import com.roodie.materialmovies.util.Utils;
import com.roodie.model.entities.MovieWrapper;
import com.roodie.model.entities.PersonCreditWrapper;
import com.roodie.model.entities.PersonWrapper;
import com.roodie.model.entities.SeasonWrapper;
import com.roodie.model.entities.ShowWrapper;
import com.roodie.model.entities.Watchable;
import com.roodie.model.util.FileLog;
import com.roodie.model.util.ImageHelper;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;
import com.squareup.picasso.Target;


public class MMoviesImageView extends ImageView {

    private static final String LOG_TAG = MMoviesImageView.class.getSimpleName();

    private ImageHandler mImageHandler;
    private boolean mAvatarMode = false;
    private boolean mAutoFade = false;
    private boolean mBlurred = false;



    public interface OnLoadedListener {

        void onSuccess(MMoviesImageView imageView, Bitmap bitmap, String imageUrl);

        void onError(MMoviesImageView imageView);
    }

    public MMoviesImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setAvatarMode(boolean mAvatarMode) {
        this.mAvatarMode = mAvatarMode;
    }

    public void setAutoFade(boolean autoFade) {
        mAutoFade = autoFade;
    }

    public void setBlurred(boolean blurred) {
        mBlurred = blurred;
    }

    private void reset() {
        setImageHandler(null);
        setImageDrawable(null);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        if (changed && canLoadImage() && mImageHandler != null && !mImageHandler.isStarted()) {
            loadUrlImmediate();
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        Picasso.with(getContext()).cancelRequest(mBitmapTarget);
        super.onDetachedFromWindow();
    }

    private boolean canLoadImage() {
        return getWidth() != 0 && getHeight() != 0;
    }

    private void loadUrlImmediate() {
        Preconditions.checkNotNull(mImageHandler, "mPicassoHandler cannot be null");

        final String url = mImageHandler.getImageUrl(this);

        if (url != null) {
            mImageHandler.markAsStarted();
            /*
            * Build Picasso  which respects user requirement of
            * only loading images over WiFi.
            * */
            RequestCreator request = Picasso.with(getContext()).load(url);
            /*
            * If isAllowedLargeDataConnection is false, will set NetworkPolicy
            * to OFFLINE  to skip the network and accept  stale images.
            */
            if (!Utils.isAllowedLargeDataConnection(getContext(), false)) {
                request.networkPolicy(NetworkPolicy.OFFLINE);
            }
            if (mImageHandler.getPlaceholderDrawable() != 0) {
                request = request.placeholder(mImageHandler.getPlaceholderDrawable());
            }
            if (mImageHandler.centerCrop()) {
                request = request.resize(getWidth(), getHeight()).centerCrop();
            } else {
                request = request.resize(getWidth(), getHeight()).centerInside();
            }
            request.into(mBitmapTarget);

                FileLog.d("images", "Loading " + url);
        }
    }


    // Cancel the loading request event.
    public void cancelLoading() {
        //TODO
    }


    public void  loadPoster(Watchable item) {
        switch (item.getWatchableType()) {
            case MOVIE:
                loadPoster((MovieWrapper) item);
                break;
            case TV_SHOW:
                loadPoster((ShowWrapper) item);
                break;
        }
    }
    /**
     * Set movie poster handler
     */
    public void loadPoster(MovieWrapper movie) {
        loadPoster(movie, null);
    }

    public void loadPoster(MovieWrapper movie, OnLoadedListener listener) {
        if (movie.hasPosterUrl()) {
            setImageHandler(new MoviePosterHandler(movie, listener));
        } else {
            reset();
            setImageResourceImpl(R.drawable.poster);
        }
    }

    /**
     * Set Tv-show poster handler
     */
    public void loadPoster(ShowWrapper show) {
        loadPoster(show, null);
    }

    public void loadPoster(ShowWrapper show, OnLoadedListener listener) {
        if (show.hasPosterUrl()) {
            setImageHandler(new ShowPosterHandler(show, listener));
        } else {
            reset();
            setImageResourceImpl(R.drawable.poster);
        }
    }

    /**
     * Set TvShows season poster handler
     */
    public void loadPoster(SeasonWrapper season) {
        loadPoster(season, null);
    }

    public void loadPoster(SeasonWrapper season, OnLoadedListener listener) {
        if (season.hasPosterUrl()) {
            setImageHandler(new SeasonPosterHandler(season, listener));
        } else {
            reset();
            setImageResourceImpl(R.drawable.poster);
        }
    }




    /**
     * Set person credit poster handler
     */
    public void loadPoster(PersonCreditWrapper person) {
        loadPoster(person, null);
    }

    public void loadPoster(PersonCreditWrapper person, OnLoadedListener listener) {
        if (!TextUtils.isEmpty(person.getPosterPath())) {
            setImageHandler(new PersonCreditHandler(person, listener));
        } else {
            reset();
        }
    }

    /**
     * Set person profile image handler
     */
    public void loadProfile(PersonWrapper person){
        loadProfile(person, null);
    }


    public void loadProfile(PersonWrapper person, OnLoadedListener listener){
        if (!TextUtils.isEmpty(person.getPictureUrl())) {
            setImageHandler(new PersonProfileHandler(person, listener));
        } else {
            reset();
            setImageResourceImpl(R.drawable.ic_profile_placeholder);
        }
    }

    public void loadBackdrop(MovieWrapper movie) {
        loadBackdrop(movie, null);
    }

    public void loadBackdrop(MovieWrapper movie, OnLoadedListener listener) {
        if (movie.hasBackdropUrl()) {
            setImageHandler(new MovieBackdropHandler(movie, listener));
        } else {
            reset();
        }
    }

    public void loadBackdrop(ShowWrapper show) {
        loadBackdrop(show, null);
    }

    public void loadBackdrop(ShowWrapper show, OnLoadedListener listener) {
        if (show.hasBackdropUrl()) {
            setImageHandler(new ShowBackdropHandler(show, listener));
        } else {
            reset();
        }
    }

    public void loadBackdrop(MovieWrapper.BackdropImage image) {
        loadBackdrop(image, null);
    }

    public void loadBackdrop(MovieWrapper.BackdropImage image, OnLoadedListener listener) {
        if (!TextUtils.isEmpty(image.url)) {
            setImageHandler(new MovieBackdropImageHandler(image, listener));
        } else {
            reset();
        }
    }

    private void setImageHandler(ImageHandler handler) {
        if (mImageHandler != null && mImageHandler.isStarted() && !mImageHandler.isFinished()) {
            Picasso.with(getContext()).cancelRequest(mBitmapTarget);
        }

        if (handler != null && Objects.equal(handler, mImageHandler)) {
            handler.setDisplayPlaceholder(false);
        }

        mImageHandler = handler;

        if (handler != null && canLoadImage()) {
            loadUrlImmediate();
        }
    }

    protected abstract static class ImageHandler<R> {
        private R mObject;
        private OnLoadedListener mListener;
        private boolean mIsStarted, mIsFinished;
        private boolean mDisplayPlaceholder = true;

        public ImageHandler(R mObject, OnLoadedListener mListener) {
            this.mObject = Preconditions.checkNotNull(mObject, "mObject cannot be null");
            this.mListener = mListener;
        }

        void markAsStarted() {
            mIsStarted = true;
        }

        void markAsFinished() {
            mIsFinished = true;
        }

        public boolean isStarted() {
            return mIsStarted;
        }

        public boolean isFinished() {
            return mIsFinished;
        }

        public boolean centerCrop() {
            return true;
        }

        public final String getImageUrl(ImageView imageView) {
            return buildUrl(mObject, imageView);
        }

        protected abstract String buildUrl(R object, ImageView imageView);

        int getPlaceholderDrawable() {
            return 0;
        }

        public void setDisplayPlaceholder(boolean displayPlaceholder) {
            mDisplayPlaceholder = displayPlaceholder;
        }

        public boolean shouldDisplayPlaceholder() {
            return mDisplayPlaceholder;
        }
    }

    //MoviePosterHandler
    private class MoviePosterHandler extends ImageHandler<MovieWrapper> {

        public MoviePosterHandler(MovieWrapper mObject, OnLoadedListener mListener) {
            super(mObject, mListener);
        }

        @Override
        protected String buildUrl(MovieWrapper object, ImageView imageView) {
            return ImageHelper.getPosterUrl(object, imageView.getWidth(), imageView.getHeight());
        }

       /* @Override
        int getPlaceholderDrawable() {
            return R.drawable.poster;
        }*/
    }

    //ShowPosterHandler
    private class ShowPosterHandler extends ImageHandler<ShowWrapper> {

        public ShowPosterHandler(ShowWrapper mObject, OnLoadedListener mListener) {
            super(mObject, mListener);
        }

        @Override
        protected String buildUrl(ShowWrapper object, ImageView imageView) {
            return ImageHelper.getPosterUrl(object, imageView.getWidth(), imageView.getHeight());
        }

        /*@Override
        int getPlaceholderDrawable() {
            return R.drawable.poster;
        }*/

    }

    //SeasonPosterHandler
    private class SeasonPosterHandler extends ImageHandler<SeasonWrapper> {

        public SeasonPosterHandler(SeasonWrapper mObject, OnLoadedListener mListener) {
            super(mObject, mListener);
        }

        @Override
        protected String buildUrl(SeasonWrapper object, ImageView imageView) {
            return ImageHelper.getPosterUrl(object, imageView.getWidth(), imageView.getHeight());
        }

      /*  @Override
        int getPlaceholderDrawable() {
            return R.drawable.poster;
        }*/

    }

    //CastProfileHandler
    private class PersonProfileHandler extends ImageHandler<PersonWrapper> {
        public PersonProfileHandler(PersonWrapper mObject, OnLoadedListener mListener) {
            super(mObject, mListener);
        }

        @Override
        protected String buildUrl(PersonWrapper object, ImageView imageView) {
            return ImageHelper.getProfileUrl(object, imageView.getWidth(), imageView.getHeight());
        }

        @Override
        int getPlaceholderDrawable() {
            return R.drawable.ic_profile_placeholder;
        }
    }

    private class PersonCreditHandler extends ImageHandler<PersonCreditWrapper> {

        PersonCreditHandler(PersonCreditWrapper credit, OnLoadedListener listener) {
            super(credit, listener);
        }

        @Override
        protected String buildUrl(PersonCreditWrapper credit, ImageView imageView) {
            return ImageHelper.getPosterUrl(credit, imageView.getWidth(), imageView.getHeight());
        }

    }

    private class MovieBackdropHandler extends ImageHandler<MovieWrapper> {

        MovieBackdropHandler(MovieWrapper movie, OnLoadedListener callback) {
            super(movie, callback);
        }

        @Override
        protected String buildUrl(MovieWrapper movie, ImageView imageView) {
            return ImageHelper.getFanartUrl(movie, imageView.getWidth(), imageView.getHeight());
        }

        @Override
        int getPlaceholderDrawable() {
            return R.color.mm_green;
        }
    }

    private class ShowBackdropHandler extends ImageHandler<ShowWrapper> {

        ShowBackdropHandler(ShowWrapper show, OnLoadedListener callback) {
            super(show, callback);
        }

        @Override
        protected String buildUrl(ShowWrapper show, ImageView imageView) {
            return ImageHelper.getFanartUrl(show, imageView.getWidth(), imageView.getHeight());
        }

        @Override
        int getPlaceholderDrawable() {
            return R.color.mm_green;
        }
    }

    private class MovieBackdropImageHandler extends ImageHandler<MovieWrapper.BackdropImage> {

        MovieBackdropImageHandler(MovieWrapper.BackdropImage backdrop, OnLoadedListener callback) {
            super(backdrop, callback);
        }

        @Override
        protected String buildUrl(MovieWrapper.BackdropImage backdrop,
                                  ImageView imageView) {
            return ImageHelper.getFanartUrl(backdrop, imageView.getWidth(), imageView.getHeight());
        }

        @Override
        public boolean centerCrop() {
            return false;
        }
    }


    private final Target mBitmapTarget = new Target() {
        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            Log.d("mmovies_image", "On bitmap loaded");
            setImageBitmapFromNetwork(bitmap, from);
            if (mImageHandler != null) {
                if (mImageHandler.mListener != null) {
                    mImageHandler.mListener.onSuccess(MMoviesImageView.this, bitmap, mImageHandler.getImageUrl(MMoviesImageView.this));
                }
                mImageHandler.markAsFinished();
            }
        }

        @Override
        public void onBitmapFailed(Drawable errorDrawable) {
            Log.d("mmovies_image", "On bitmap failed");
            if (mImageHandler != null) {
                if (mImageHandler.mListener != null) {
                    mImageHandler.mListener.onError(MMoviesImageView.this);
                }
                mImageHandler.markAsFinished();
            }

        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {
            Log.d("mmovies_image", "On prepare load");
            if (mImageHandler == null || mImageHandler.shouldDisplayPlaceholder()) {
                setImageDrawableImpl(placeHolderDrawable);
            }
        }
    };


    void setImageBitmapFromNetwork(final Bitmap bitmap, Picasso.LoadedFrom loadedFrom) {
        //final boolean fade = mAutoFade && loadedFrom != Picasso.LoadedFrom.MEMORY;
       // final boolean blur = mBlurred;
        final Drawable currentDrawable = getDrawable();

        if (mAutoFade) {
            if (currentDrawable == null || mImageHandler.getPlaceholderDrawable() != 0) {
                // If we have no current drawable, or it is a placeholder drawable. Just fade in
                setVisibility(View.INVISIBLE);
                setImageBitmapImpl(bitmap);
                AnimUtils.Fade.show(this);
            } else {
                    AnimUtils.startCrossFade(this, currentDrawable,
                            new BitmapDrawable(getResources(), bitmap));
            }
        } else if (mBlurred) {
            AnimUtils.makeBlur(this, bitmap);

        } else {
            setImageBitmapImpl(bitmap);
        }

    }

    void setImageBitmapImpl(final Bitmap bitmap) {
        if (mAvatarMode) {
            setImageDrawable(new RoundedDrawable(bitmap));
        } else {
            setImageBitmap(bitmap);
        }
    }

    void setImageDrawableImpl(final Drawable drawable) {
        if (mAvatarMode && drawable instanceof BitmapDrawable) {
            setImageBitmapImpl(((BitmapDrawable) drawable).getBitmap());
        } else {
            setImageDrawable(drawable);
        }
    }

    void setImageResourceImpl(int resId) {
        if (mAvatarMode) {
            BitmapDrawable d = (BitmapDrawable) getResources().getDrawable(resId);
            assert d != null;
            setImageDrawable(new RoundedDrawable(d.getBitmap()));
        } else {
            setImageResource(resId);
        }
    }

}


