package com.roodie.materialmovies.util;

import android.content.Context;

import com.google.common.base.Preconditions;
import com.roodie.model.util.VisitManager;

/**
 * Created by Roodie on 05.09.2015.
 */
public class MMoviesVisitManager implements VisitManager {

    private final Context mContext;

    public MMoviesVisitManager(Context context) {
        this.mContext = Preconditions.checkNotNull(context, "Context cannot be null");
    }

    @Override
    public boolean isFirstVisitPerformed() {
        return MMoviesPreferences.isSetFirstVisitAsPerformed(mContext);
    }

    @Override
    public void recordFirstVisitPerformed() {
        MMoviesPreferences.setFirstVisitPerformed(mContext, true);
    }
}
