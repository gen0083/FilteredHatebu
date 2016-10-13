package jp.gcreate.sample.daggersandbox;

import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscription;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Copyright 2016 G-CREATE
 */

public class RxObservableTest {
    private Observable<String> source = Observable.just("one", "two", "three");

    private Observable<String> getSource() {
        return source
                .doOnCompleted(new Action0() {
                    @Override
                    public void call() {
                        System.out.println(" - doOnCompleted. thread:" + Thread.currentThread());
                    }
                })
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        System.out
                                .println(" - doOnSubscribe. thread:" + Thread.currentThread());
                    }
                });
    }

    @Test
    public void doOnSubscribeとdoOnCompletedとsubscribeの処理順確認() throws InterruptedException {
        final CountDownLatch latch = new CountDownLatch(1);
        getSource().subscribeOn(Schedulers.io())
                   .subscribe(new Action1<String>() {
                       @Override
                       public void call(String s) {
                           System.out.println(
                                   " - subscribe " + s + " thread:" + Thread.currentThread());
                           latch.countDown();
                       }
                   });
        latch.await(2, TimeUnit.SECONDS);
    }

    @Test
    public void doOnSubscribeのみの場合は何も起こらない() {
        // subscribeされていないため、値が流れない
        source.doOnSubscribe(new Action0() {
            @Override
            public void call() {
                System.out.println(" - doOnSubscribe");
            }
        });
    }

    @Test
    public void onCompleted後はunsubscribeされる() throws InterruptedException {
        final CountDownLatch latch = new CountDownLatch(1);
        Subscription s = source
                .subscribeOn(Schedulers.io())
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        System.out.println(s);
                        try {
                            TimeUnit.SECONDS.sleep(1);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        System.out.println(" - error");
                    }
                }, new Action0() {
                    @Override
                    public void call() {
                        System.out.println(" - completed");
                        latch.countDown();
                    }
                });
        assertThat(s.isUnsubscribed(), is(false));
        latch.await(10, TimeUnit.SECONDS);
        TimeUnit.SECONDS.sleep(1);
        assertThat(s.isUnsubscribed(), is(true));
    }

    @Test
    public void unsubscribeで処理がキャンセルされる() throws InterruptedException {
        Subscription s = source.subscribeOn(Schedulers.io())
                     .subscribe(new Action1<String>() {
                         @Override
                         public void call(String s) {
                             System.out.println(s);
                             try {
                                 TimeUnit.SECONDS.sleep(1);
                             } catch (InterruptedException e) {
                                 System.out.println(" - interrupted.");
                             }
                         }
                     }, new Action1<Throwable>() {
                         @Override
                         public void call(Throwable throwable) {
                             System.out.println(" - error cause:" + throwable.getCause());
                         }
                     }, new Action0() {
                         @Override
                         public void call() {
                             System.out.println(" - completed");
                         }
                     });
        assertThat(s.isUnsubscribed(), is(false));
        s.unsubscribe();
        TimeUnit.SECONDS.sleep(3);
    }
}
