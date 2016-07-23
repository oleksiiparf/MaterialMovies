package com.roodie.materialmovies.views.fragments.base;

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.arellomobile.mvp.MvpDelegate;
import com.roodie.materialmovies.R;
import com.roodie.materialmovies.util.UiUtils;
import com.roodie.materialmovies.views.activities.BaseActivity;
import com.roodie.materialmovies.views.custom_views.MMoviesToolbar;
import com.roodie.model.Display;

import butterknife.ButterKnife;

/**
 * Created by Roodie on 01.07.2015.
 */
public abstract class BaseMvpFragment extends Fragment implements BaseUiView {

    private MvpDelegate<? extends BaseMvpFragment> mMvpDelegate;

    private MMoviesToolbar mToolbar;

    @LayoutRes
    protected abstract int getLayoutRes();

    protected abstract void attachUiToPresenter();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getMvpDelegate().onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        FrameLayout wrapper = new FrameLayout(getActivity());
        inflater.inflate(getLayoutRes(), wrapper, true);
        ButterKnife.inject(this, wrapper);
        return wrapper;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getMvpDelegate().onDestroy();
    }

    @Override
    public void onStart() {
        getMvpDelegate().onStart();
        attachUiToPresenter();
        super.onStart();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        getMvpDelegate().onSaveInstanceState(outState);
    }

    @Override
    public void onStop() {
        super.onStop();
        getMvpDelegate().onStop();
    }

    /**
     * @return The {@link MvpDelegate} being used by this Fragment.
     */
    public MvpDelegate getMvpDelegate()
    {
        if (mMvpDelegate == null)
        {
            mMvpDelegate = new MvpDelegate<>(this);
        }
        return mMvpDelegate;
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
      super.onViewCreated(view, savedInstanceState);
      mToolbar = (MMoviesToolbar) view.findViewById(R.id.toolbar);
        if (mToolbar != null) {
            setSupportActionBar(mToolbar);
            mToolbar.setToolbarTitleTypeface();
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        UiUtils.getInstance().applyFontToMenu(menu, getActivity());
    }

    protected  void setSupportActionBar(Toolbar toolbar) {
        setSupportActionBar(toolbar, true);
    }

    protected void setSupportActionBar(Toolbar toolbar, boolean handle) {
        getBaseActivity().setSupportActionBar(toolbar, handle);
    }

    public MMoviesToolbar getToolbar() {
        return mToolbar;
    }

    protected Display getDisplay() {
        return getBaseActivity().getDisplay();
    }

    protected BaseActivity getBaseActivity() {
        return (BaseActivity)getActivity();
    }

}
