package jp.gcreate.product.filteredhatebu;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Copyright 2016 G-CREATE
 */

public class ListTest {
    public static class TestModel {
        long id;
        String value;

        @Override
        public String toString() {
            return "<" + this.getClass().getSimpleName() + "@" + hashCode() + ">"
                    + "(id:" + id + ", value:" + value + ")";
        }

        public TestModel(long id, String value) {
            this.id = id;
            this.value = value;


        }
    }

    List<TestModel> list = new ArrayList<>();

    @Before
    public void setUp() {
        list.add(new TestModel(1, "one"));
        list.add(new TestModel(2, "two"));
        list.add(new TestModel(3, "three"));
    }

    @Test
    public void update_enhanced_for_loop() {
        TestModel update = new TestModel(2, "second");
        int i = -1;
        for (TestModel target : list) {
            i++;
            assertThat(target, is(list.get(i)));
            if (target.id == update.id) {
                list.set(i, update);
                break;
            }
        }
        assertThat(list.get(0).value, is("one"));
        assertThat(list.get(1).value, is("second"));
        assertThat(list.get(2).value, is("three"));
    }

    @Test
    public void update_for_loop() {
        TestModel update = new TestModel(2, "second");
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).id == update.id) {
                list.set(i, update);
                break;
            }
        }
        assertThat(list.get(0).value, is("one"));
        assertThat(list.get(1).value, is("second"));
        assertThat(list.get(2).value, is("three"));
    }

    @Test
    public void update_iterator() {
        TestModel update = new TestModel(2, "second");
        Iterator  i = list.iterator();
        int index = -1;
        while(i.hasNext()) {
            TestModel target = (TestModel) i.next();
            index++;
            if (target.id == update.id) {
                list.set(index, update);
                break;
            }
        }
        assertThat(list.get(0).value, is("one"));
        assertThat(list.get(1).value, is("second"));
        assertThat(list.get(2).value, is("three"));
    }

    @Test
    public void delete_enhanced_for_loop() {
        int deleteId = 2;
        int i = 0;
        for (TestModel target : list) {
            if (target.id == deleteId) {
                list.remove(i);
                break;
            }
            i++;
        }
        assertThat(list.size(), is(2));
        assertThat(list.get(0).value, is("one"));
        assertThat(list.get(1).value, is("three"));
    }

    @Test
    public void delete_for_loop() {
        int deleteId = 2;
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).id == deleteId) {
                list.remove(i);
                break;
            }
        }
        assertThat(list.size(), is(2));
        assertThat(list.get(0).value, is("one"));
        assertThat(list.get(1).value, is("three"));
    }

    @Test
    public void delete_iterator() {
        int deleteId = 2;
        Iterator it = list.iterator();
        while (it.hasNext()) {
            TestModel target = (TestModel) it.next();
            if (target.id == deleteId) {
                it.remove();
                break;
            }
        }
        assertThat(list.size(), is(2));
        assertThat(list.get(0).value, is("one"));
        assertThat(list.get(1).value, is("three"));
    }
}
