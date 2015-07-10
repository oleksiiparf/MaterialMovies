package com.roodie.materialmovies.views.custom_views;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.roodie.model.entities.MovieWrapper;

public class MMoviesImageView extends ImageView {

    public interface OnLoadedListener {

        public void onSuccess(MMoviesImageView imageView, Bitmap bitmap);

        public void onError(MMoviesImageView imageView);
    }

    public MMoviesImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void loadPoster(MovieWrapper movie) {
        loadPoster(movie, null);
    }

    public void loadPoster(MovieWrapper movie, OnLoadedListener listener) {
    }


}


