package jp.gcreate.product.filteredhatebu;

import org.junit.Test;

import java.util.concurrent.TimeUnit;

import rx.Single;
import rx.SingleSubscriber;
import rx.Subscription;
import rx.functions.Action1;
import rx.observers.TestSubscriber;
import rx.schedulers.Schedulers;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Copyright 2016 G-CREATE
 */

public class RxSingleTest {
    private Single<String> source = Single.create(new Single.OnSubscribe<String>() {
        @Override
        public void call(SingleSubscriber<? super String> singleSubscriber) {
            try {
                // emulate weighted work
                TimeUnit.SECONDS.sleep(2);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            singleSubscriber.onSuccess("test");
        }
    });

    private Single<String> getSource() {
        return source.doOnSuccess(new Action1<String>() {
            @Override
            public void call(String s) {
                System.out.println(" - doOnSuccess() " + s + " thread:" + Thread.currentThread());
            }
        });
    }

    @Test
    public void doOnSuccessとsubscribeの処理順確認() {
        getSource().subscribeOn(Schedulers.io())
                   .subscribe(new Action1<String>() {
                       @Override
                       public void call(String s) {
                           System.out.println(
                                   " - subscribe " + s + " thread:" + Thread.currentThread());
                       }
                   });
    }

    @Test
    public void doOnSuccessのみの場合は何も起こらない() {
        // subscribeされていないため、値が流れずdoOnSuccessも呼ばれない
        source.doOnSuccess(new Action1<String>() {
            @Override
            public void call(String s) {
                System.out.println(s);
            }
        });
    }

    @Test
    public void onSuccess後はunsubscribeされる() throws InterruptedException {
        TestSubscriber<String> test = new TestSubscriber<>();
        Subscription s = source
                .subscribeOn(Schedulers.io())
                .subscribe(test);
        assertThat(s.isUnsubscribed(), is(false));
        test.awaitTerminalEvent(3, TimeUnit.SECONDS);
        test.assertValue("test");
        test.onCompleted();
        assertThat(s.isUnsubscribed(), is(true));
    }
}
