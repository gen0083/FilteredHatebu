package jp.gcreate.product.filteredhatebu.ui.feeddetail;

import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.Espresso;
import android.support.test.espresso.IdlingResource;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import jp.gcreate.product.filteredhatebu.R;
import jp.gcreate.product.filteredhatebu.model.HatebuFeedItem;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.doesNotExist;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.allOf;

/**
 * Copyright 2016 G-CREATE
 */
@LargeTest
@RunWith(AndroidJUnit4.class)
public class HatebuFeedDetailActivityTest {
    @Rule
    public ActivityTestRule<HatebuFeedDetailActivity> activityTestRule =
            new ActivityTestRule<HatebuFeedDetailActivity>(HatebuFeedDetailActivity.class, false, false);

    @Test
    public void コメントが表示される() {
        HatebuFeedItem item = new HatebuFeedItem();
        item.setLink("http://test.com/");

        activityTestRule.launchActivity(
                HatebuFeedDetailActivity.createIntent(InstrumentationRegistry.getTargetContext(),
                                                      item));
        AdapterIdlingResource idlingResource = new AdapterIdlingResource(activityTestRule.getActivity().getAdapter());
        Espresso.registerIdlingResources(idlingResource);

        onView(allOf(withId(R.id.comment),
                     withText("test")))
                .check(matches(isDisplayed()));

        Espresso.unregisterIdlingResources(idlingResource);
    }

    @Test
    public void コメントなしのケース() {
        //どの記事を開いても最初に開いたURLのコメントが表示されたケースに対応するテストコード
        //ついでにコメントなしのケースをチェックするテストケースでもある

        HatebuFeedItem item = new HatebuFeedItem();
        item.setLink("http://aaa.com/");

        activityTestRule.launchActivity(
                HatebuFeedDetailActivity.createIntent(InstrumentationRegistry.getTargetContext(),
                                                      item));
//        AdapterIdlingResource idlingResource = new AdapterIdlingResource(activityTestRule.getActivity().getAdapter());
//        Espresso.registerIdlingResources(idlingResource);

        // TODO: コメントなしを実装したら、メッセージが表示されているかどうかで判定する
        onView(allOf(withId(R.id.comment),
                     withText("test")))
                .check(doesNotExist());
//        Espresso.unregisterIdlingResources(idlingResource);
    }

    private static class AdapterIdlingResource implements IdlingResource {
        private ResourceCallback callback;
        private BookmarkCommentsAdapter adapter;

        public AdapterIdlingResource(BookmarkCommentsAdapter adapter) {
            this.adapter = adapter;
        }

        @Override
        public String getName() {
            return this.getClass().getSimpleName();
        }

        @Override
        public boolean isIdleNow() {
            // TODO: 要改善！ アダプターサイズで判定すると、コメントなしのケースで待機できない
            boolean isIdle = adapter.getItemCount() != 0;
            if (isIdle && callback != null) {
                callback.onTransitionToIdle();
            }
            return isIdle;
        }

        @Override
        public void registerIdleTransitionCallback(ResourceCallback callback) {
            this.callback = callback;
        }
    }
}