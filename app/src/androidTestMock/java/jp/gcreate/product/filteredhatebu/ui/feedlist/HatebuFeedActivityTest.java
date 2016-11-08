package jp.gcreate.product.filteredhatebu.ui.feedlist;


import android.support.test.espresso.Espresso;
import android.support.test.espresso.IdlingResource;
import android.support.test.espresso.ViewInteraction;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.hamcrest.core.IsInstanceOf;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import jp.gcreate.product.filteredhatebu.R;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.doesNotExist;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.anything;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class HatebuFeedActivityTest {
    private IdlingResource idlingResource;

    @Rule
    public ActivityTestRule<HatebuFeedActivity> mActivityTestRule =
            new ActivityTestRule<>(HatebuFeedActivity.class);

    @Before
    public void setUp() {
        mActivityTestRule.getActivity().presenter.initialzieFilterRepository();
        idlingResource = new ItemSetIdlingResource( mActivityTestRule.getActivity());
        Espresso.registerIdlingResources(idlingResource);
    }

    @After
    public void tearDown() {
        mActivityTestRule.getActivity().presenter.initialzieFilterRepository();
        Espresso.unregisterIdlingResources(idlingResource);
    }

    @Test
    public void フィルタを追加したら該当記事が表示されない() {
        onView(allOf(withId(R.id.recycler_view), isDisplayed()))
                .perform(actionOnItemAtPosition(0, click()));

        onView(withId(R.id.add_filter_button))
                .perform(click());

        onData(anything())
                .atPosition(0)
                .perform(click());

        pressBack();

        ViewInteraction textView = onView(
                allOf(withId(R.id.title), withText("test1"),
                      childAtPosition(
                              childAtPosition(
                                      IsInstanceOf.<View>instanceOf(
                                              android.widget.LinearLayout.class),
                                      0),
                              1),
                      isDisplayed()));
        textView.check(doesNotExist());
    }

    @Test
    public void サブディレクトリ込で登録後test1は表示test3は表示されない() {
        ViewInteraction recyclerView = onView(
                allOf(withId(R.id.recycler_view), isDisplayed()));
        recyclerView.perform(actionOnItemAtPosition(2, click()));

        ViewInteraction appCompatButton = onView(
                allOf(withId(R.id.add_filter_button), withText("フィルタを追加"), isDisplayed()));
        appCompatButton.perform(click());

        onData(anything())
                .atPosition(1)
                .perform(click());

        pressBack();

        ViewInteraction textView = onView(
                allOf(withId(R.id.title), withText("test1"),
                      childAtPosition(
                              childAtPosition(
                                      IsInstanceOf.<View>instanceOf(
                                              android.widget.LinearLayout.class),
                                      0),
                              1),
                      isDisplayed()));
        textView.check(matches(isDisplayed()));

        ViewInteraction textView2 = onView(
                allOf(withId(R.id.title), withText("test3"),
                      childAtPosition(
                              childAtPosition(
                                      IsInstanceOf.<View>instanceOf(
                                              android.widget.LinearLayout.class),
                                      0),
                              1),
                      isDisplayed()));
        textView2.check(doesNotExist());
    }

    @Test
    public void カテゴリフィードが表示できる() {
        onView(allOf(withText("テクノロジー"), isDisplayed())).perform(click());

        ViewInteraction textView = onView(
                allOf(withId(R.id.title),
                      withText("category test"),
                      childAtPosition(
                              childAtPosition(
                                      IsInstanceOf.<View>instanceOf(
                                              android.widget.LinearLayout.class),
                                      0),
                              1),
                      isDisplayed()));
        textView.check(
                matches(withText("category test")));
    }

    @Test
    public void フィルタ追加後カテゴリの記事も非表示になる() {
        onView(allOf(withId(R.id.recycler_view), isDisplayed()))
                .perform(actionOnItemAtPosition(0, click()));

        onView(
                allOf(withId(R.id.add_filter_button), withText("フィルタを追加"),
                      isDisplayed()))
                .perform(click());

        // test.comをフィルタに追加
        onData(anything())
                .atPosition(0)
                .perform(click());

        pressBack();

        onView(allOf(withId(R.id.title), withText("test1"),
                     childAtPosition(
                             childAtPosition(
                                     IsInstanceOf.<View>instanceOf(
                                             android.widget.LinearLayout.class),
                                     0),
                             1),
                     isDisplayed()))
                .check(doesNotExist());

        onView(allOf(withText("テクノロジー"), isDisplayed())).perform(click());

        onView(allOf(withId(R.id.title),
                     withText("category test"),
                     childAtPosition(
                             childAtPosition(
                                     IsInstanceOf.<View>instanceOf(
                                             android.widget.LinearLayout.class),
                                     0),
                             1),
                     isDisplayed()))
                .check(doesNotExist());

        Espresso.unregisterIdlingResources(idlingResource);
    }

    private static Matcher<View> childAtPosition(
            final Matcher<View> parentMatcher, final int position) {

        return new TypeSafeMatcher<View>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("Child at position " + position + " in parent ");
                parentMatcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(View view) {
                ViewParent parent = view.getParent();
                return parent instanceof ViewGroup && parentMatcher.matches(parent)
                       && view.equals(((ViewGroup) parent).getChildAt(position));
            }
        };
    }

    private class ItemSetIdlingResource implements IdlingResource {
        private ResourceCallback   callback;
        private HatebuFeedActivity activity;

        public ItemSetIdlingResource(HatebuFeedActivity activity) {
            this.activity = activity;
        }

        @Override
        public String getName() {
            return this.getClass().getSimpleName() + activity;
        }

        @Override
        public boolean isIdleNow() {
            boolean idle = !activity.isFeedLoading();
            if (idle && callback != null) {
                callback.onTransitionToIdle();
            }
            return idle;
        }

        @Override
        public void registerIdleTransitionCallback(ResourceCallback callback) {
            this.callback = callback;
        }
    }
}
