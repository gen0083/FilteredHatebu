package jp.gcreate.sample.daggersandbox;

import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import rx.Single;
import rx.SingleSubscriber;
import rx.Subscription;
import rx.functions.Action1;
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
        final CountDownLatch latch = new CountDownLatch(1);
        Subscription s = source
                .subscribeOn(Schedulers.io())
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        try {
                            TimeUnit.SECONDS.sleep(2);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        assertThat(s, is("test"));
                        latch.countDown();
                    }
                });
        assertThat(s.isUnsubscribed(), is(false));
        latch.await();
        TimeUnit.SECONDS.sleep(1);
        assertThat(s.isUnsubscribed(), is(true));
    }
}
