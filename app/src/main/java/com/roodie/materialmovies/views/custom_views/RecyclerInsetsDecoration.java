package com.roodie.materialmovies.views.custom_views;

import android.content.Context;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.roodie.materialmovies.R;

/**
 * Created by Roodie on 12.08.2015.
 */
public class RecyclerInsetsDecoration extends RecyclerView.ItemDecoration {

    private int mSpacing;

    public RecyclerInsetsDecoration(Context context) {
        mSpacing = context.getResources().getDimensionPixelSize(R.dimen.movie_grid_spacing);
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {

        //We can supply forced insets for each item view here in the Rect
        super.getItemOffsets(outRect, view, parent, state);
        outRect.set(mSpacing, mSpacing, mSpacing, mSpacing);
    }
}