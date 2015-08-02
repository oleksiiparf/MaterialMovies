package com.roodie.materialmovies.views.fragments.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.roodie.materialmovies.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Roodie on 02.08.2015.
 */
public abstract class BaseTabFragment extends BaseFragment {

    private static final String SAVE_SELECTED_TAB = "selected_tab";

    private ViewPager mViewPager;
    private TabLayout mSlidingTabStrip;
    private TabPagerAdapter mAdapter;

    private int mCurrentItem;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_viewpager, container, false);

        mAdapter = new TabPagerAdapter(getChildFragmentManager());

        mViewPager = (ViewPager) view.findViewById(R.id.viewpager);
        //mViewPager.setOffscreenPageLimit(1);

       // mViewPager.setPageMargin(getResources().getDimensionPixelSize(R.dimen.spacing_minor));

        mSlidingTabStrip = (TabLayout) view.findViewById(R.id.tabs);


        if (savedInstanceState != null) {
            mCurrentItem = savedInstanceState.getInt(SAVE_SELECTED_TAB);
        }

        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(SAVE_SELECTED_TAB, mCurrentItem);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onPause() {
        super.onPause();
        mCurrentItem = mViewPager.getCurrentItem();
    }

    protected ViewPager getViewPager() {
        return mViewPager;
    }

    protected TabPagerAdapter getAdapter() {
        return mAdapter;
    }

    protected TabLayout getSlidingTabStrip() {
        return mSlidingTabStrip;
    }

    protected void setFragments(List<Fragment> fragments) {
        mAdapter.setFragments(fragments);
        mViewPager.setAdapter(mAdapter);
        //mViewPager.setCurrentItem(mCurrentItem);
        mSlidingTabStrip.setupWithViewPager(mViewPager);
    }

    protected  abstract String getTabTitle(int position);

    protected class TabPagerAdapter extends FragmentPagerAdapter {

        private final ArrayList<Fragment> mFragments = new ArrayList<>();

        public TabPagerAdapter(FragmentManager fm) {
            super(fm);
           // mFragments = new ArrayList<>();
        }

       void setFragments(List<Fragment> fragments)  {
           mFragments.clear();
           mFragments.addAll(fragments);
           notifyDataSetChanged();
       }

        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }


        @Override
        public CharSequence getPageTitle(int position) {
            return getTabTitle(position);
        }

        @Override
        public final int getCount() {
            return mFragments.size();
        }
    }



}
