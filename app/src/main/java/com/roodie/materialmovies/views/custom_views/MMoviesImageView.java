package com.roodie.materialmovies.views.custom_views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.roodie.materialmovies.R;
import com.roodie.materialmovies.drawable.RoundedDrawable;
import com.roodie.model.entities.MovieWrapper;
import com.roodie.model.entities.PersonCreditWrapper;
import com.roodie.model.entities.PersonWrapper;
import com.roodie.model.entities.ShowWrapper;
import com.roodie.model.util.ImageHelper;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;
import com.squareup.picasso.Target;


public class MMoviesImageView extends ImageView {

    private static final String LOG_TAG = MMoviesImageView.class.getSimpleName();

    private ImageHandler mImageHandler;
    private boolean mAvatarMode = false;

    public interface OnLoadedListener {

        public void onSuccess(MMoviesImageView imageView, Bitmap bitmap, String imageUrl);

        public void onError(MMoviesImageView imageView);
    }

    public MMoviesImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setAvatarMode(boolean mAvatarMode) {
        this.mAvatarMode = mAvatarMode;
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

            RequestCreator request = Picasso.with(getContext()).load(url);
            if (mImageHandler.getPlaceholderDrawable() != 0) {
                request = request.placeholder(mImageHandler.getPlaceholderDrawable());
            }
            if (mImageHandler.centerCrop()) {
                request = request.resize(getWidth(), getHeight()).centerCrop();
            } else {
                request = request.resize(getWidth(), getHeight()).centerInside();
            }
            request.into(mBitmapTarget);

                Log.d("PhilmImageView", "Loading " + url);
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
        }
    }

    /**
     * Set show poster handler
     */
    public void loadPoster(ShowWrapper show) {
        loadPoster(show, null);
    }

    public void loadPoster(ShowWrapper show, OnLoadedListener listener) {
        if (show.hasPosterUrl()) {
            setImageHandler(new ShowPosterHandler(show, listener));
        } else {
            reset();
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
            //Log.d(LOG_TAG, "On bitmap loaded");
            setImageBitmapImpl(bitmap);
            if (mImageHandler != null) {
                if (mImageHandler.mListener != null) {
                    mImageHandler.mListener.onSuccess(MMoviesImageView.this, bitmap, mImageHandler.getImageUrl(MMoviesImageView.this));
                }
                mImageHandler.markAsFinished();
            }
        }

        @Override
        public void onBitmapFailed(Drawable errorDrawable) {
            //Log.d(LOG_TAG, "On bitmap failed");
            if (mImageHandler != null) {
                if (mImageHandler.mListener != null) {
                    mImageHandler.mListener.onError(MMoviesImageView.this);
                }
                mImageHandler.markAsFinished();
            }

        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {
            //Log.d(LOG_TAG, "On prepare load");
            if (mImageHandler == null || mImageHandler.shouldDisplayPlaceholder()) {
                setImageDrawableImpl(placeHolderDrawable);
            }
        }
    };


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
            setImageDrawable(new RoundedDrawable(d.getBitmap()));
        } else {
            setImageResource(resId);
        }
    }




}


