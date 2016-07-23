package com.roodie.materialmovies.detail;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.Espresso;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.roodie.materialmovies.MMoviesDisplay;
import com.roodie.materialmovies.R;
import com.roodie.materialmovies.views.activities.MovieActivity;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.doesNotExist;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Created by Roodie on 13.07.2016.
 */

@RunWith(AndroidJUnit4.class)
public class MovieDetailActivityTest {

    public static String TEST_MOVIE_ID = "293660";
    @Rule
    // third parameter is set to false which means the activity is not started automatically
    public ActivityTestRule<MovieActivity> mRule =
            new ActivityTestRule(MovieActivity.class, true, false);

    @Before
    public void testIntentPrep() {
        Intent intent = new Intent();
        intent.putExtra(MMoviesDisplay.PARAM_ID, TEST_MOVIE_ID);
        mRule.launchActivity(intent);

        registerIdlingResource();
    }

    @Test
    public void testLoadMovieInfoView() {
        onView(withId(R.id.recycler_view)).check(matches(not(isDisplayed())));
        onView(withId(R.id.not_available_view)).check(matches(not(isDisplayed())));

        onView(withId(R.id.loading_view)).check(matches(isDisplayed()));
        onView(withId(R.id.fanart_image)).check(matches(isDisplayed()));
    }

    @Test
    public void testContainsDetailMovieFragment() {
        registerIdlingResource();

        Fragment movieFragment =
                mRule.getActivity().getFragmentManager().findFragmentById(R.id.fragment_main);
        assertThat(movieFragment, is(notNullValue()));
    }


    @Test
    public void testRotateScreenMulti() {
        onView(withId(R.id.content_view)).check(matches(isDisplayed()));

        // Set landscape orientation
        rotateScreen();
        // For mobile screen
        onView(withId(R.id.left_container)).check(doesNotExist());
        // For tablet screen
        //onView(withId(R.id.left_container)).check(matches(isDisplayed()));
    }

    private void rotateScreen() {
        Context context = InstrumentationRegistry.getTargetContext();
        int orientation = context.getResources().getConfiguration().orientation;

        Activity activity = mRule.getActivity();
        activity.setRequestedOrientation(
                (orientation == Configuration.ORIENTATION_PORTRAIT) ?
                        ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE : ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    /**
     * Unregister your Idling Resource so it can be garbage collected and does not leak any memory.
     */
    @After
    public void unregisterIdlingResource() {
        Espresso.unregisterIdlingResources(
                mRule.getActivity().getCountingIdlingResource());
    }

    /**
     * Convenience method to register an IdlingResources with Espresso. IdlingResource resource is
     * a great way to tell Espresso when your app is in an idle state. This helps Espresso to
     * synchronize your test actions, which makes tests significantly more reliable.
     */
    private void registerIdlingResource() {
        Espresso.registerIdlingResources(
                mRule.getActivity().getCountingIdlingResource());
    }
}
