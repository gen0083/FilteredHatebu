package jp.gcreate.product.filteredhatebu;

import android.support.test.runner.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;
import io.realm.exceptions.RealmPrimaryKeyConstraintException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

/**
 * Copyright 2016 G-CREATE
 */

@RunWith(AndroidJUnit4.class)
public class RealmOperationTest {
    @Before
    public void setUp() {
        deleteDefaultRealm();
    }

    @After
    public void tearDown() {
        deleteDefaultRealm();
    }

    @Test
    public void insert() {
        Realm realm = Realm.getDefaultInstance();
        try {
            RealmResults<RealmOperationTestModel> actual = realm
                    .where(RealmOperationTestModel.class)
                    .findAll();
            assertThat(actual.size(), is(0));

            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    RealmOperationTestModel model = new RealmOperationTestModel(1, "test.com/");
                    realm.copyToRealm(model);
                }
            });

            actual = realm.where(RealmOperationTestModel.class).findAll();
            assertThat(actual.size(), is(1));
        } finally {
            realm.close();
        }
    }

    @Test
    public void update() {
        Realm realm = Realm.getDefaultInstance();
        try {

            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    RealmOperationTestModel model = new RealmOperationTestModel(1, "test.com/");
                    realm.copyToRealm(model);
                }
            });

            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    RealmOperationTestModel model = new RealmOperationTestModel(2, "abc.jp/");
                    realm.copyToRealm(model);
                }
            });

            RealmResults<RealmOperationTestModel> result = realm
                    .where(RealmOperationTestModel.class)
                    .findAll();
            assertThat(result.size(), is(2));
            assertThat(result.get(0).value, is("test.com/"));
            assertThat(result.get(1).value, is("abc.jp/"));

            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    RealmResults<RealmOperationTestModel> result = realm
                            .where(RealmOperationTestModel.class)
                            .equalTo("value", "abc.jp/")
                            .findAll();
                    RealmOperationTestModel target = result.get(0);
                    target.value = "hoge.jp/";
                    realm.copyToRealmOrUpdate(target);
                }
            });

            result = realm.where(RealmOperationTestModel.class).findAll();
            assertThat(result.size(), is(2));
            assertThat(result.get(0).value, is("test.com/"));
            assertThat(result.get(1).value, is("hoge.jp/"));
        } finally {
            realm.close();
        }
    }

    @Test(expected = RealmPrimaryKeyConstraintException.class)
    public void insert_duplicated_primary_key() {
        Realm realm = Realm.getDefaultInstance();
        try {
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    RealmOperationTestModel model = new RealmOperationTestModel(1, "one.com/");
                    realm.insert(model);
                    model = new RealmOperationTestModel(1, "two.com/");
                    realm.insert(model);
                }
            });

            RealmResults<RealmOperationTestModel> result = realm
                    .where(RealmOperationTestModel.class)
                    .findAll();
            assertThat(result.size(), is(1));

        } finally {
            realm.close();
        }
    }

    @Test
    public void delete() {
        Realm realm = Realm.getDefaultInstance();
        try {
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    RealmOperationTestModel model = new RealmOperationTestModel(1, "one.com/");
                    realm.insert(model);
                    model = new RealmOperationTestModel(2, "two.com/");
                    realm.insert(model);
                    model = new RealmOperationTestModel(3, "three.com/");
                    realm.insert(model);
                }
            });

            RealmResults<RealmOperationTestModel> result = realm
                    .where(RealmOperationTestModel.class)
                    .findAll();
            assertThat(result.size(), is(3));

            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    RealmResults<RealmOperationTestModel> target = realm
                            .where(RealmOperationTestModel.class)
                            .equalTo("id", 2)
                            .findAll();
                    assertThat(target.size(), is(1));
                    assertThat(target.get(0).value, is("two.com/"));
                    target.get(0).deleteFromRealm();
                }
            });

            result = realm.where(RealmOperationTestModel.class)
                          .findAll();
            assertThat(result.size(), is(2));

        } finally {
            realm.close();
        }
    }

    private void deleteDefaultRealm() {
        Realm              realm  = Realm.getDefaultInstance();
        RealmConfiguration config = realm.getConfiguration();
        realm.close();
        Realm.deleteRealm(config);
    }
}
