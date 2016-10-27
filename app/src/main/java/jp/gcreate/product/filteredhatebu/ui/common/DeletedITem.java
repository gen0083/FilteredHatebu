package jp.gcreate.product.filteredhatebu.ui.common;

/**
 * Copyright 2016 G-CREATE
 */

public class DeletedITem<T> {
    private int position;
    private T item;

    public DeletedITem(int position, T item) {
        this.position = position;
        this.item = item;
    }

    public int getPosition() {
        return position;
    }

    public T getItem() {
        return item;
    }
}
