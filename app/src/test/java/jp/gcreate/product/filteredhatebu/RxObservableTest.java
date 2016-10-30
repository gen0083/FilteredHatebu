package jp.gcreate.product.filteredhatebu;

import org.junit.Test;

import java.util.Arrays;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscription;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func1;
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

    @Test
    public void test() {
        Observable.range(1, 10)
                  .filter(new Func1<Integer, Boolean>() {
                      @Override
                      public Boolean call(Integer integer) {
                          System.out.println("on filter " + integer);
                          return integer % 2 == 0;
                      }
                  })
                  .doOnNext(new Action1<Integer>() {
                      @Override
                      public void call(Integer integer) {
                          System.out.println("on doOnNext1 " + integer);
                      }
                  })
                  .map(new Func1<Integer, String>() {
                      @Override
                      public String call(Integer integer) {
                          return "No." + integer;
                      }
                  })
                  .doOnNext(new Action1<String>() {
                      @Override
                      public void call(String s) {
                          System.out.println("on doOnNext2 " + s);
                      }
                  })
                  .subscribe(new Action1<String>() {
                      @Override
                      public void call(String s) {
                          System.out.println("on subscribe:" + s);
                      }
                  });
    }

    @Test
    public void concatMapで値が流れてこない場合concatしたObservableが流れるか確認() {
        // concatMapに値が流れないので当然concatしたObservableは流れない
        Observable.from(Arrays.asList(1, 3, 5))
                  .filter(new Func1<Integer, Boolean>() {
                      @Override
                      public Boolean call(Integer integer) {
                          System.out.println("on filter " + integer);
                          return integer % 2 == 0;
                      }
                  })
                  .concatMap(new Func1<Integer, Observable<String>>() {
                      @Override
                      public Observable<String> call(Integer integer) {
                          System.out.println("on concatMap " + integer);
                          return getSource();
                      }
                  })
                  .subscribe(new Action1<String>() {
                      @Override
                      public void call(String s) {
                          System.out.println("on subscribe " + s);
                      }
                  }, new Action1<Throwable>() {
                      @Override
                      public void call(Throwable throwable) {
                          System.out.println("on subscribe onError");
                          throwable.printStackTrace();
                      }
                  }, new Action0() {
                      @Override
                      public void call() {
                          System.out.println("on subscribe onComplete");
                      }
                  });
    }
}
