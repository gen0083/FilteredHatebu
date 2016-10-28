package jp.gcreate.product.filteredhatebu.ui.feedlist;


import android.content.Context;
import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.ViewInteraction;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;
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
import jp.gcreate.product.filteredhatebu.di.AppDataModule;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.doesNotExist;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withClassName;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withParent;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.is;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class HatebuFeedActivityTest {

    @Rule
    public ActivityTestRule<HatebuFeedActivity> mActivityTestRule = new ActivityTestRule<>(
            HatebuFeedActivity.class, false, false);

    @Before
    public void setUp() {
        // clear database before launch activity
        Context context = InstrumentationRegistry.getTargetContext();
        context.deleteDatabase(AppDataModule.ORMA_FILE);
        Intent i = new Intent(context, HatebuFeedActivity.class);
        mActivityTestRule.launchActivity(i);
    }

    @After
    public void tearDown() {
        mActivityTestRule.getActivity().filterRepository.deleteAll();
    }

    @Test
    public void フィルタを追加したら該当記事が表示されない() {
        ViewInteraction recyclerView = onView(
                allOf(withId(R.id.recycler_view), isDisplayed()));
        recyclerView.perform(actionOnItemAtPosition(0, click()));

        ViewInteraction appCompatButton = onView(
                allOf(withId(R.id.add_filter_button), withText("フィルタを追加"), isDisplayed()));
        appCompatButton.perform(click());

        ViewInteraction appCompatTextView = onView(
                allOf(withId(android.R.id.text1), withText("test.com/"),
                      childAtPosition(
                              allOf(withClassName(
                                      is("com.android.internal.app.AlertController$RecycleListView")),
                                    withParent(withClassName(is("android.widget.FrameLayout")))),
                              0),
                      isDisplayed()));
        appCompatTextView.perform(click());

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

        ViewInteraction appCompatTextView = onView(
                allOf(withId(android.R.id.text1), withText("test.com/test/"),
                      childAtPosition(
                              allOf(withClassName(
                                      is("com.android.internal.app.AlertController$RecycleListView")),
                                    withParent(withClassName(is("android.widget.FrameLayout")))),
                              1),
                      isDisplayed()));
        appCompatTextView.perform(click());

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

        ViewInteraction appCompatTextView = onView(
                allOf(withId(android.R.id.text1), withText("test.com/"),
                      childAtPosition(
                              allOf(withClassName(
                                      is("com.android.internal.app.AlertController$RecycleListView")),
                                    withParent(withClassName(is("android.widget.FrameLayout")))),
                              0),
                      isDisplayed()));
        appCompatTextView.perform(click());

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
}
