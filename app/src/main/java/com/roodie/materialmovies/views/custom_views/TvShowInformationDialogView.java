package com.roodie.materialmovies.views.custom_views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.roodie.materialmovies.R;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

/**
 * Created by Roodie on 20.08.2015.
 */
public class TvShowInformationDialogView extends RelativeLayout implements Target {

    private ImageButton mLikeButton;
    private ImageButton mShareButton;
    private TextView mYearTextView;
    private TextView mRatingTextView;
    private ImageView mCoverImageView;
    private TextView mSummaryTextView;


    public TvShowInformationDialogView(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.item_show_dialog, this, true);

        mLikeButton = (ImageButton) findViewById(R.id.like_button);
        mShareButton = (ImageButton) findViewById(R.id.share_button);
        mYearTextView = (TextView) findViewById(R.id.year_text_view);
        mRatingTextView = (TextView) findViewById(R.id.rating_text_view);
        mCoverImageView = (ImageView) findViewById(R.id.show_cover_image);
        mSummaryTextView = (TextView) findViewById(R.id.summary_text_view);

    }


    public void setYear(CharSequence text) {
        mYearTextView.setText(text);
    }

    public void setRating(CharSequence text) {
        mRatingTextView.setText(text);
    }

    public void setSummary(CharSequence text) {
        mSummaryTextView.setText(text);
    }

    public TextView getYearTextView() {
        return mYearTextView;
    }

    public TextView getRatingTextView() {
        return mRatingTextView;
    }

    public ImageView getCoverImageView() {
        return mCoverImageView;
    }

    public TextView getSummaryTextView() {
        return mSummaryTextView;
    }

    public ImageButton getLikeButton() {
        return mLikeButton;
    }

    public ImageButton getShareButton() {
        return mShareButton;
    }

    @Override
    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
        mRatingTextView.setCompoundDrawablesWithIntrinsicBounds(
                new BitmapDrawable(getResources(), bitmap),
                null, null, null);
    }

    @Override
    public void onBitmapFailed(Drawable errorDrawable) {
        mRatingTextView.setCompoundDrawables(null, null, null, null);
    }

    @Override
    public void onPrepareLoad(Drawable placeHolderDrawable) {
        mRatingTextView.setCompoundDrawables(null, null, null, null);
    }
}
