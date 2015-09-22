package com.roodie.materialmovies.views.custom_views;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.roodie.materialmovies.R;

/**
 * Created by Roodie on 16.08.2015.
 */
public class RatingBarLayout extends RelativeLayout {

    private final TextView mRatingValueTextView;
    private final TextView mRatingRangeTextView;
    private final TextView mRatingVotesTextView;
    private final TextView mRatingLabelTextView;

    private String mRatingValue;
    private int mRatingTotalVotes;

    public RatingBarLayout(Context context) {
        this(context, null);
    }

    public RatingBarLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.include_rating_bar, this);

        mRatingValueTextView = (TextView) findViewById(R.id.text_view_rating_value);
        mRatingRangeTextView = (TextView) findViewById(R.id.text_view_rating_range);
        mRatingRangeTextView.setText(Integer.toString(10));
        mRatingVotesTextView = (TextView) findViewById(R.id.text_view_rating_votes);
        mRatingLabelTextView = (TextView) findViewById(R.id.text_view_rating_label);
        mRatingLabelTextView.setText(context.getText(R.string.tmdb));
    }

    public void setRatingValue(String value) {
        if (TextUtils.isEmpty(mRatingValueTextView.getText()) ||
                mRatingValue != value) {
            mRatingValue = value;
            mRatingValueTextView.setText(mRatingValue);
        }
    }

    public void setRatingRange(Integer range) {
        if (range != null) {
            mRatingRangeTextView.setText("/" + Integer.toString(range));
        }

    }

    public void setRatingVotes(int totalVotes) {
        if (TextUtils.isEmpty(mRatingVotesTextView.getText()) ||
                mRatingTotalVotes != totalVotes) {
            mRatingTotalVotes = totalVotes;

            mRatingVotesTextView.setText(getResources().getQuantityString(R.plurals.votes, mRatingTotalVotes, mRatingTotalVotes));
        }
    }

    public void setWhiteTheme() {
        int colorW = getResources().getColor(R.color.mm_white_1);

        mRatingValueTextView.setTextColor(colorW);
        mRatingRangeTextView.setTextColor(colorW);
        mRatingVotesTextView.setTextColor(colorW);
        mRatingLabelTextView.setTextColor(colorW);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
    }
}
