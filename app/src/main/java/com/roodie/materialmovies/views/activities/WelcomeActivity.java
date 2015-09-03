package com.roodie.materialmovies.views.activities;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;

import com.roodie.materialmovies.R;

import butterknife.Bind;

/**
 * Created by Roodie on 03.09.2015.
 */
public class WelcomeActivity extends BaseActivity {

    private static final int WELCOME_ACTIVITY_PAGER_SIZE = 3;

    @Bind({R.id.viewpager})
    ViewPager mViewPager;

    @Bind({R.id.welcome_view_1})
    View mWelcomeViewPage1;

    @Bind({R.id.welcome_view_2})
    View mWelcomeViewPage2;

    @Bind({R.id.welcome_view_3})
    View mWelcomeViewPage3;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    @Override
    protected int getContentViewLayoutId() {
        return R.layout.activity_welcome;
    }

    private class WelcomePagerAdapter extends PagerAdapter {

        public WelcomePagerAdapter() {
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            switch (position) {
                default:
                    return null;
                case 0:
                    return WelcomeActivity.this.mWelcomeViewPage1;
                case 1:
                    return WelcomeActivity.this.mWelcomeViewPage2;
                case 2:
                    return WelcomeActivity.this.mWelcomeViewPage3;
            }
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }
    }
}
