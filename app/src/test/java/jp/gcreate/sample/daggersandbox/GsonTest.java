package jp.gcreate.sample.daggersandbox;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.junit.Before;
import org.junit.Test;

import java.util.Calendar;
import java.util.Date;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * Copyright 2016 G-CREATE
 */

public class GsonTest {
    private Gson gson;
    private Date TEST_DATE;

    @Before
    public void setUp() {
        gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd HH:mm:ss")
                .serializeNulls()
                .create();
        Calendar cal = Calendar.getInstance();
        cal.set(2017, Calendar.JANUARY, 3, 10, 20, 30);
        TEST_DATE = cal.getTime();
    }

    @Test
    public void object_from_json() {
        GsonTestObject obj = gson.fromJson(
                "{\"name\":\"test\",\"email\":\"test@test.com\",\"date\":\"2017-05-23 12:33:45\",\"num\":1}",
                GsonTestObject.class);
        assertThat(obj.name, is("test"));
        assertThat(obj.email, is("test@test.com"));
        System.out.println(obj.date);
        assertThat(obj.num, is(1));
    }

    @Test
    public void object_from_json_with_null_value() {
        GsonTestObject obj = gson.fromJson(
                "{\"name\":\"test\",\"email\":\"test@test.com\",\"num\":1}",
                GsonTestObject.class);
        assertThat(obj.name, is("test"));
        assertThat(obj.email, is("test@test.com"));
        System.out.println(obj.date);
        assertThat(obj.num, is(1));
    }

    @Test
    public void object_to_json() {
        GsonTestObject obj = new GsonTestObject("hoge", "fuga@fuga.com", TEST_DATE, 2);
        String json = gson.toJson(obj);
        System.out.println(json);
        assertThat(json, is("{\"name\":\"hoge\",\"email\":\"fuga@fuga.com\",\"date\":\"2017-01-03 10:20:30\",\"num\":2}"));
    }

    @Test
    public void object_to_json_with_null_value() {
        GsonTestObject obj = new GsonTestObject("hoge", "fuga@fuga.com", null, 2);
        String json = gson.toJson(obj);
        System.out.println(json);
        assertThat(json, is("{\"name\":\"hoge\",\"email\":\"fuga@fuga.com\",\"date\":null,\"num\":2}"));
    }

    public static class GsonTestObject {
        String name;
        String email;
        Date   date;
        int    num;

        public GsonTestObject(String name, String email, Date date, int num) {
            this.name = name;
            this.email = email;
            this.date = date;
            this.num = num;
        }

        public GsonTestObject(String name, String email, int num) {
            this(name, email, new Date(), num);
        }
    }
}
