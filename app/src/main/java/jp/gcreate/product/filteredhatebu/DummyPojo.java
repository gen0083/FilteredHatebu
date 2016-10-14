package jp.gcreate.product.filteredhatebu;

/**
 * Copyright 2016 G-CREATE
 */

public class DummyPojo {
    public final long time;
    public final String name;

    public DummyPojo(long time, String name) {
        this.time = time;
        this.name = name;
    }

    @Override
    public String toString() {
        return "{time: " + time + " name:" + name  +"}";
    }
}
