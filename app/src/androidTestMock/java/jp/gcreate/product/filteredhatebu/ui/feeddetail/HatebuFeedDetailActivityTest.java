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
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.allOf;

/**
 * Copyright 2016 G-CREATE
 * TODO: テストに失敗するので直す
 *       しかしテストしている意味が見いだせないのでいっそのこと消したほうがいいんじゃないかな
 */
@LargeTest
@RunWith(AndroidJUnit4.class)
public class HatebuFeedDetailActivityTest {
    private IdlingResource idlingResource;
    @Rule
    public ActivityTestRule<HatebuFeedDetailActivity> activityTestRule =
            new ActivityTestRule<HatebuFeedDetailActivity>(HatebuFeedDetailActivity.class, false,
                                                           false);

    private void registerIdlingResource() {
        idlingResource = new CommentLoadIdlingResource(activityTestRule.getActivity());
        Espresso.registerIdlingResources(idlingResource);
    }

    private void unregisterIdlingResouce() {
        Espresso.unregisterIdlingResources(idlingResource);
    }

    @Test
    public void コメントが表示される() {
        // set up
        HatebuFeedItem item = new HatebuFeedItem();
        item.setLink("http://test.com/");
        activityTestRule.launchActivity(HatebuFeedDetailActivity.createIntent(
                InstrumentationRegistry.getTargetContext(), item));
        registerIdlingResource();
        // assertion
        onView(allOf(withId(R.id.comment),
                     withText("test")))
                .check(matches(isDisplayed()));
        // tear down
        unregisterIdlingResouce();
    }

    @Test
    public void コメントなしのケース() {
        //どの記事を開いても最初に開いたURLのコメントが表示されたケースに対応するテストコード
        //ついでにコメントなしのケースをチェックするテストケースでもある
        // set up
        HatebuFeedItem item = new HatebuFeedItem();
        item.setLink("http://aaa.com/");
        activityTestRule.launchActivity(HatebuFeedDetailActivity.createIntent(
                InstrumentationRegistry.getTargetContext(), item));
        registerIdlingResource();
        // assertion
        onView(withId(R.id.comment_status))
                .check(matches(isDisplayed()));
        // tear down
        unregisterIdlingResouce();
    }

    @Test
    public void コメントがnullのケース() {
        // 対象のページがコメント不許可の設定がされている場合、bookmarksがjsonレスポンスに含まれない場合がある
        // set up
        HatebuFeedItem item = new HatebuFeedItem();
        // www.test4.comはbookmarksが存在しないjsonを返却するようにモックしている
        item.setLink("http://www.test4.com/hoge");
        activityTestRule.launchActivity(HatebuFeedDetailActivity.createIntent(
                InstrumentationRegistry.getTargetContext(), item));
        registerIdlingResource();
        // assertion
        onView(withId(R.id.comment_status))
                .check(matches(isDisplayed()));
        // tear down
        unregisterIdlingResouce();
    }

    private static class CommentLoadIdlingResource implements IdlingResource {
        private ResourceCallback         callback;
        private HatebuFeedDetailActivity activity;

        public CommentLoadIdlingResource(HatebuFeedDetailActivity activity) {
            this.activity = activity;
        }

        @Override
        public String getName() {
            return this.getClass().getSimpleName();
        }

        @Override
        public boolean isIdleNow() {
            boolean isIdle = activity.isCommentLoadFinished();
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