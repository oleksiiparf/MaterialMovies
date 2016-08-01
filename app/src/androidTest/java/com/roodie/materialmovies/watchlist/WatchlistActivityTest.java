package com.roodie.materialmovies.watchlist;

import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.Espresso;
import android.support.test.espresso.ViewInteraction;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.espresso.matcher.BoundedMatcher;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;

import com.roodie.materialmovies.R;
import com.roodie.materialmovies.views.activities.WatchlistActivity;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.pressImeActionButton;
import static android.support.test.espresso.action.ViewActions.swipeLeft;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.contrib.DrawerActions.open;
import static android.support.test.espresso.contrib.DrawerMatchers.isClosed;
import static android.support.test.espresso.contrib.DrawerMatchers.isOpen;
import static android.support.test.espresso.contrib.NavigationViewActions.navigateTo;
import static android.support.test.espresso.contrib.RecyclerViewActions.scrollTo;
import static android.support.test.espresso.matcher.ViewMatchers.hasDescendant;
import static android.support.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static android.support.test.espresso.matcher.ViewMatchers.isDescendantOfA;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withContentDescription;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static com.google.common.base.Preconditions.checkArgument;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.allOf;

/**
 * Created by Roodie on 09.07.2016.
 */

@RunWith(AndroidJUnit4.class)
@LargeTest
public class WatchlistActivityTest{


    /**
     * A custom {@link Matcher} which matches an item in a {@link RecyclerView} by its text.
     *
     * <p>
     * View constraints:
     * <ul>
     * <li>View must be a child of a {@link RecyclerView}
     * <ul>
     *
     * @param itemText the text to match
     * @return Matcher that matches text in the given view
     */
    private Matcher<View> withItemText(final String itemText) {
        checkArgument(!TextUtils.isEmpty(itemText), "itemText cannot be null or empty");
        return new TypeSafeMatcher<View>() {
            @Override
            public boolean matchesSafely(View item) {
                return allOf(
                        isDescendantOfA(isAssignableFrom(RecyclerView.class)),
                        withText(itemText)).matches(item);
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("is isDescendantOfA RV with text " + itemText);
            }
        };
    }

    /**
     *  Match a Toolbar by its title.
     */
    private static Matcher<Object> withToolbarTitle(
            final Matcher<CharSequence> textMatcher) {
        return new BoundedMatcher<Object, Toolbar>(Toolbar.class) {
            @Override public boolean matchesSafely(Toolbar toolbar) {
                return textMatcher.matches(toolbar.getTitle());
            }
            @Override public void describeTo(Description description) {
                description.appendText("with toolbar title: ");
                textMatcher.describeTo(description);
            }
        };
    }


    private static ViewInteraction matchToolbarTitle(CharSequence title) {
        return onView(isAssignableFrom(Toolbar.class))
                .check(matches(withToolbarTitle(is(title))));
    }


    /**
     * {@link ActivityTestRule} is a JUnit {@link Rule @Rule} to launch your activity under test.
     *
     * <p>
     * Rules are interceptors which are executed for each test method and are important building
     * blocks of Junit tests.
     */
    @Rule
    public ActivityTestRule<WatchlistActivity> mWatchlistActivityTestRule =
            new ActivityTestRule<>(WatchlistActivity.class);


    @Before
    public void setUp() {
        registerIdlingResource();
    }


    @Test
    public void testClickOnSearch() throws Exception {

        String searchTitle = "Super";
        onView(withId(R.id.menu_search))
                .perform(click());

        //Check if Search container is displayed
        onView(withId(R.id.search_view_container)).check(matches(isDisplayed()));

        // Type new search title
        onView(withId(R.id.search_edit)).perform(typeText(searchTitle), pressImeActionButton());

        // Verify title
        onView(withId(R.id.search_edit)).check(matches(withText(searchTitle)));

    }

    @Test
    public void testClickOnLibraryNavigationItem_ShowLibraryScreen() {
        //Open Drawer to click on navigation.
        onView(withId(R.id.drawer_layout))
                .check(matches(isClosed(Gravity.START))) // Left Drawer should be closed.
                .perform(open()); // Open Drawer

        // Start library screen.
        onView(withId(R.id.navigation_view))
                .perform(navigateTo(R.id.menu_watched));

        // Check that library Activity was opened.
        String expectedLibraryTitleText = InstrumentationRegistry.getTargetContext()
                .getString(R.string.watched_title);
        matchToolbarTitle(expectedLibraryTitleText);


    }

    @Test
    public void testClickOnAndroidHomeIcon_OpensNavigation() {
        // Check that left drawer is closed at startup
        onView(withId(R.id.drawer_layout))
                .check(matches(isClosed(Gravity.START))); // Left Drawer should be closed.

        // Open Drawer
        String navigateUpDesc = mWatchlistActivityTestRule.getActivity()
                .getString(android.support.v7.appcompat.R.string.abc_action_bar_up_description);
        // or R.string.drawer_open_content_desc
        onView(withContentDescription(navigateUpDesc)).perform(click());

        // Check if drawer is open
        onView(withId(R.id.drawer_layout))
                .check(matches(isOpen(Gravity.START))); // Left drawer is open open.
    }

    @Test
    public void testPerformRecyclerLibraryItemClick() {

        String deapdool = "Deadpool";

        //Open Drawer to click on navigation.
        onView(withId(R.id.drawer_layout))
                .check(matches(isClosed(Gravity.START))) // Left Drawer should be closed.
                .perform(open()); // Open Drawer

        // Start library screen.
        onView(withId(R.id.navigation_view))
                .perform(navigateTo(R.id.menu_watched));

        onView(allOf(withId(R.id.recycler_view))).check(matches(isDisplayed()));


        // Scroll recycler to item, by finding its description
        onView(withId(R.id.recycler_view)).perform(
                scrollTo(hasDescendant(withText(deapdool))));

        // Verify item is displayed on screen
        onView(withItemText(deapdool)).check(matches(isDisplayed()));

        //Scroll recycler to item position 9 and click
        onView(allOf(withId(R.id.recycler_view)))
                .perform(RecyclerViewActions.scrollToPosition(9)).perform(RecyclerViewActions.actionOnItemAtPosition(9, click()));

    }

    @Test
    public void testPagerDoubleSwipeLeft() {
        String upcomingTitle = mWatchlistActivityTestRule.getActivity()
                .getString(R.string.upcoming_title); // Title of the third item in pager adapter

        //Check if TabLayout contains tab with title
        onView(withId(R.id.tabs))
                .check(matches(hasDescendant(withText(upcomingTitle))));

        // Double swipe left on ViewPager
        onView(withId(R.id.viewpager))
                .perform(swipeLeft())
                .perform(swipeLeft());

    }

    /**
     * Unregister your Idling Resource so it can be garbage collected and does not leak any memory.
     */
    @After
    public void unregisterIdlingResource() {
        Espresso.unregisterIdlingResources(
                mWatchlistActivityTestRule.getActivity().getCountingIdlingResource());
    }

    /**
     * Convenience method to register an IdlingResources with Espresso. IdlingResource resource is
     * a great way to tell Espresso when your app is in an idle state. This helps Espresso to
     * synchronize your test actions, which makes tests significantly more reliable.
     */
    private void registerIdlingResource() {
        Espresso.registerIdlingResources(
                mWatchlistActivityTestRule.getActivity().getCountingIdlingResource());
    }

}
