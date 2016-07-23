package com.roodie.materialmovies.views.custom_views.recyclerview;

import android.content.Context;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.roodie.materialmovies.R;
import com.roodie.materialmovies.mvp.views.UiView;

/**
 * Implementation of {@link RecyclerView.ItemDecoration}, which depends on {@link UiView.NavigationGridType} item
 */
public class RecyclerInsetsDecoration extends RecyclerView.ItemDecoration {

    private int mSpacing;

    public RecyclerInsetsDecoration(Context context, UiView.NavigationGridType type) {
        switch (type) {
            case MOVIES:
                mSpacing = context.getResources().getDimensionPixelSize(R.dimen.movie_grid_spacing);
                break;
            case SHOWS:
                mSpacing = context.getResources().getDimensionPixelSize(R.dimen.show_grid_spacing);
                break;
            case WATCHED:
                mSpacing = context.getResources().getDimensionPixelSize(R.dimen.show_grid_spacing);
                break;

        }
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {

        //We can supply forced insets for each item view here in the Rect
        super.getItemOffsets(outRect, view, parent, state);
        outRect.set(mSpacing, mSpacing, mSpacing, mSpacing);
    }
}