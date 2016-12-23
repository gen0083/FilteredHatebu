package jp.gcreate.product.filteredhatebu.ui.feedlist;


import android.support.test.espresso.Espresso;
import android.support.test.espresso.IdlingResource;
import android.support.test.espresso.matcher.BoundedMatcher;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.v7.widget.RecyclerView;
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
import jp.gcreate.product.filteredhatebu.databinding.ItemHatebuFeedBinding;
import jp.gcreate.product.filteredhatebu.ui.common.DataBindingViewHolder;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.doesNotExist;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.contrib.RecyclerViewActions.scrollToHolder;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class HatebuFeedActivityTest {
    private IdlingResource              idlingResource;
    private HatebuFeedActivityPresenter presenter;

    @Rule
    public ActivityTestRule<HatebuFeedActivity> mActivityTestRule =
            new ActivityTestRule<>(HatebuFeedActivity.class);

    @Before
    public void setUp() {
        presenter = mActivityTestRule.getActivity().presenter;
        presenter.initialzieFilterRepository();
        idlingResource = new ItemSetIdlingResource(mActivityTestRule.getActivity());
        Espresso.registerIdlingResources(idlingResource);
    }

    @After
    public void tearDown() {
        presenter.initialzieFilterRepository();
        Espresso.unregisterIdlingResources(idlingResource);
    }

    @Test
    public void フィルタ無しの場合表示されている() {
        onView(allOf(withId(R.id.url), withText("test.com/hoge")))
                .check(matches(isDisplayed()));
        onView(allOf(withId(R.id.url), withText("test.com/test/is/difficult")))
                .check(matches(isDisplayed()));
        onView(allOf(withId(R.id.recycler_view), isDisplayed()))
                .perform(scrollToHolder(withHolderUrlText("www.test.com/test/manager")));
        onView(allOf(withId(R.id.url), withText("www.test.com/test/manager")))
                .check(matches(isDisplayed()));
    }

    @Test
    public void フィルタを追加したら該当記事が表示されない() {
        presenter.addFilter("test.com/");
        onView(allOf(withId(R.id.url), withText("test.com/hoge")))
                .check(doesNotExist());
        onView(allOf(withId(R.id.url), withText("test.com/test/is/difficult")))
                .check(doesNotExist());
        onView(allOf(withId(R.id.url), withText("www.test.com/test/manager")))
                .check(doesNotExist());
        // ちなみにこれだとテストとして不適当
        // doesNotExistでは表示されていないから存在していないのか、
        // アイテムとして存在しているがViewHolderにBindされていないから存在しないのかがわからない
        // 例えば以下のtest30.com/はアイテムとして存在しているが、
        // アイテムの最後尾に存在するためRecyclerViewにBindされていないためにこのテストは通る
        // アイテムの存在・不存在のチェックには使ってはいけない
        onView(allOf(withId(R.id.url), withText("test30.com/")))
                .check(doesNotExist());
    }

    @Test
    public void サブディレクトリ込で登録した場合test01はフィルタされない() {
        presenter.addFilter("test.com/test/");
        onView(allOf(withId(R.id.url), withText("test.com/hoge"),
                     isDisplayed()))
                .check(matches(isDisplayed()));

        onView(allOf(withId(R.id.url), withText("test.com/test/is/difficult"),
                     childAtPosition(
                             childAtPosition(
                                     IsInstanceOf.<View>instanceOf(
                                             android.widget.LinearLayout.class),
                                     0),
                             1),
                     isDisplayed()))
                .check(doesNotExist());
    }

    @Test
    public void カテゴリフィードが表示できる() {
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
                .check(matches(withText("category test")));
    }

    @Test
    public void フィルタ追加後カテゴリの記事も非表示になる() {
        presenter.addFilter("test.com/");
        // test.comがフィルタされて非表示になったことをチェック
        onView(allOf(withId(R.id.url), withText("test.com/hoge"),
                     childAtPosition(
                             childAtPosition(
                                     IsInstanceOf.<View>instanceOf(
                                             android.widget.LinearLayout.class),
                                     0),
                             1),
                     isDisplayed()))
                .check(doesNotExist());

        // 総合以外のカテゴリでtest.comの記事が非表示になっているか確認
        onView(allOf(withText("テクノロジー"), isDisplayed())).perform(click());
        onView(allOf(withId(R.id.url),
                     withText("test.com/a/b/c"),
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

    public static Matcher<RecyclerView.ViewHolder> withHolderUrlText(final String text) {
        return new BoundedMatcher<RecyclerView.ViewHolder, DataBindingViewHolder>
                (DataBindingViewHolder.class) {

            @Override
            public void describeTo(Description description) {
                description.appendText("No ViewHolder found with text: " + text);
            }

            @Override
            protected boolean matchesSafely(DataBindingViewHolder item) {
                ItemHatebuFeedBinding binding = (ItemHatebuFeedBinding) item.getBinding();
                if (binding.url == null) {
                    return false;
                }
                return binding.url.getText().toString().contains(text);
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
