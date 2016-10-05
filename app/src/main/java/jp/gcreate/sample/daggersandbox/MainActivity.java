package jp.gcreate.sample.daggersandbox;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;

import javax.inject.Inject;

import jp.gcreate.sample.daggersandbox.api.HatebuService;
import jp.gcreate.sample.daggersandbox.databinding.ActivityMainBinding;
import jp.gcreate.sample.daggersandbox.di.ActivityComponent;
import jp.gcreate.sample.daggersandbox.model.HatebuEntry;
import jp.gcreate.sample.daggersandbox.recycler.BookmarksAdapter;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private BookmarksAdapter adapter;
    ActivityComponent component;
    @Inject
    String        injectedString;
    @Inject
    DummyPojo     pojo;
    @Inject
    HatebuService service;
    Subscription subscription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        component = MyApplication.getActivityComponent(this);
        component.inject(this);

        subscription = new CompositeSubscription();

        adapter = new BookmarksAdapter(this);
        binding.recyclerView.setAdapter(adapter);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    protected void onStart() {
        super.onStart();
        subscription = service
                .getEntry("http://developer.hatena.ne.jp/ja/documents/bookmark/apis/getinfo")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<HatebuEntry>() {
                    @Override
                    public void call(HatebuEntry hatebuEntry) {
                        binding.setEntry(hatebuEntry);
                        adapter.setList(hatebuEntry.getBookmarks());
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        Log.e("test", throwable.getMessage());
                        throwable.printStackTrace();
                    }
                });
    }

    @Override
    protected void onStop() {
        super.onStop();
        subscription.unsubscribe();
    }
}
