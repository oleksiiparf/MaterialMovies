package com.roodie.materialmovies.views.listeners;

/**
 * Created by Roodie on 12.08.2015.
 */
        import android.view.View;

public interface RecyclerItemClickListener {

    public void onClick(View view, int position);

    public void onPopupMenuClick(View view, int position);
}
