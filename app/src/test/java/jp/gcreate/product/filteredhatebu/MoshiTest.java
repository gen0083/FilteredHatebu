package jp.gcreate.product.filteredhatebu;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Copyright 2016 G-CREATE
 */

public class MoshiTest {
    private Moshi moshi;

    @Before
    public void setUp() {
        moshi = new Moshi.Builder().build();
    }

    @Test
    public void object_from_json() throws IOException {
        JsonAdapter<MoshiTestObject> adapter = moshi.adapter(MoshiTestObject.class);
        MoshiTestObject target = adapter.fromJson("{\"hoge\":\"abc\",\"fuga\":\"def\",\"moge\":2}");
        System.out.println(target);
        assertThat(target.hoge, is("abc"));
        assertThat(target.fuga, is("def"));
        assertThat(target.moge, is(2));
    }

    @Test
    public void object_from_json_with_null_filed() throws IOException {
        JsonAdapter<MoshiTestObject> adapter = moshi.adapter(MoshiTestObject.class);
        MoshiTestObject target = adapter.fromJson("{\"hoge\":\"abc\",\"moge\":2}");
        System.out.println(target);
        assertThat(target.hoge, is("abc"));
        assertThat(target.fuga, nullValue());
        assertThat(target.moge, is(2));
    }

    @Test
    public void object_to_json() {
        MoshiTestObject target = new MoshiTestObject("abc", "def", 2);
        JsonAdapter<MoshiTestObject> adapter = moshi.adapter(MoshiTestObject.class);
        String json = adapter.toJson(target);
        assertThat(json, is("{\"fuga\":\"def\",\"hoge\":\"abc\",\"moge\":2}"));
    }

    @Test
    public void object_to_json_with_null_value() {
        MoshiTestObject target = new MoshiTestObject("abc", null, 2);
        JsonAdapter<MoshiTestObject> adapter = moshi.adapter(MoshiTestObject.class);
        String json = adapter.toJson(target);
        assertThat(json, is("{\"hoge\":\"abc\",\"moge\":2}"));
        json = adapter.toJson(new MoshiTestObject("abc", "", 2));
        assertThat(json, is("{\"fuga\":\"\",\"hoge\":\"abc\",\"moge\":2}"));
    }

    public static class MoshiTestObject {
        String hoge;
        String fuga;
        int moge;

        public MoshiTestObject(String hoge, String fuga, int moge) {
            this.hoge = hoge;
            this.fuga = fuga;
            this.moge = moge;
        }
    }
}
