package com.roodie.materialmovies.views.listeners;

/**
 * Created by Roodie on 12.08.2015.
 */
        import android.view.View;

public interface RecyclerItemClickListener {

    void onClick(View view, int position);

    void onPopupMenuClick(View view, int position);
}
