package com.roodie.materialmovies.views.fragments.base;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.marshalchen.ultimaterecyclerview.UltimateRecyclerviewViewHolder;
import com.roodie.materialmovies.R;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Roodie on 01.07.2015.
 */
public abstract class BaseGridFragment<VH extends UltimateRecyclerviewViewHolder, M extends Serializable>
        extends RecyclerFragment<VH, M> {

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getRecyclerView().setHasFixedSize(false);
        enableEmptyViewPolicy();
        enableLoadMore();
        mAdapter = createAdapter(new ArrayList<M>());
        mUltimateRecyclerView.setItemViewCacheSize(mAdapter.getAdditionalItems());
        mUltimateRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onEmptyViewShow(View mView) {
        ImageView icon = (ImageView) mView.findViewById(R.id.empty_screen_icon);
        icon.setImageResource(R.drawable.ic_compass_white_96dp);
        TextView title = (TextView) mView.findViewById(R.id.empty_screen_title);
        title.setText(R.string.error_no_connection);
        TextView body = (TextView) mView.findViewById(R.id.empty_screen_body);
        body.setText(R.string.error_no_connection_body);
        TextView action = (TextView) mView.findViewById(R.id.empty_screen_action);
        action.setText(R.string.menu_reload);
        action.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onRefreshData(true);
            }
        });
    }

}

