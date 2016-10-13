package jp.gcreate.sample.daggersandbox;

import android.support.test.runner.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;
import jp.gcreate.sample.daggersandbox.model.UriFilter;

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
        Realm                   realm  = Realm.getDefaultInstance();
        RealmResults<UriFilter> actual = realm.where(UriFilter.class).findAll();
        assertThat(actual.size(), is(0));

        realm.beginTransaction();
        UriFilter model = new UriFilter("test.com/");
        realm.copyToRealm(model);
        realm.commitTransaction();

        actual = realm.where(UriFilter.class).findAll();
        assertThat(actual.size(), is(1));
        realm.close();
    }

    @Test
    public void update() {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        UriFilter model = new UriFilter("test.com/");
        realm.copyToRealm(model);
        realm.commitTransaction();

        realm.beginTransaction();
        model = new UriFilter("abc.jp/");
        realm.copyToRealm(model);
        realm.commitTransaction();

        RealmResults<UriFilter> result = realm.where(UriFilter.class).findAll();
        assertThat(result.size(), is(2));
        assertThat(result.get(0).getFilter(), is("test.com/"));
        assertThat(result.get(1).getFilter(), is("abc.jp/"));

        model = new UriFilter("hoge.jp/");
        realm.beginTransaction();
        realm.copyToRealmOrUpdate(model);
        realm.commitTransaction();

        result = realm.where(UriFilter.class)
                      .equalTo("filter", "abc.jp/")
                      .findAll();
        result.get(0).deleteFromRealm();

        result = realm.where(UriFilter.class).findAll();
        assertThat(result.size(), is(2));
        assertThat(result.get(0).getFilter(), is("test.com/"));
        assertThat(result.get(1).getFilter(), is("hoge.jp/"));

        realm.close();
    }

    private void deleteDefaultRealm() {
        Realm              realm  = Realm.getDefaultInstance();
        RealmConfiguration config = realm.getConfiguration();
        realm.close();
        Realm.deleteRealm(config);
    }
}
