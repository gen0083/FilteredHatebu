package jp.gcreate.product.filteredhatebu;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Copyright 2016 G-CREATE
 */

public class ArrayListTest {
    @Test
    public void containsAll_differentCase() {
        List<String> a = new ArrayList<>();
        List<String> b = new ArrayList<>();
        Collections.addAll(a, "abc", "def");
        Collections.addAll(b, "def", "ghi");
        assertThat(a.containsAll(b), is(false));
    }

    @Test
    public void containsAll_sameCase() {
        List<String> a = new ArrayList<>();
        List<String> b = new ArrayList<>();
        Collections.addAll(a, "abc", "def");
        Collections.addAll(b, "abc", "def");
        assertThat(a.containsAll(b), is(true));
    }

    @Test
    public void containsAll_sameCase_with_differentInstance() {
        List<Pojo> a = new ArrayList<>();
        List<Pojo> b = new ArrayList<>();
        a.add(new Pojo(1, "abc"));
        a.add(new Pojo(2, "aaa"));
        b.add(new Pojo(1, "abc"));
        b.add(new Pojo(2, "aaa"));
        // this returns false because containsAll checks with equals methods.
        assertThat(a.containsAll(b), is(false));
    }

    @Test
    public void containsAll_sameCase_with_differentInstance_overrideEqualsMethod() {
        List<Pojo2> a = new ArrayList<>();
        List<Pojo2> b = new ArrayList<>();
        a.add(new Pojo2(1, "abc"));
        a.add(new Pojo2(2, "aaa"));
        b.add(new Pojo2(1, "abc"));
        b.add(new Pojo2(2, "aaa"));
        assertThat(a.containsAll(b), is(true));
    }

    private static class Pojo {
        long id;
        String value;

        Pojo(long id, String value) {
            this.id = id;
            this.value = value;
        }
    }

    private static class Pojo2 extends Pojo {

        Pojo2(long id, String value) {
            super(id, value);
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof Pojo)) return false;
            Pojo t = (Pojo) obj;
            return this.id == t.id && this.value.equals(t.value);
        }
    }
}
