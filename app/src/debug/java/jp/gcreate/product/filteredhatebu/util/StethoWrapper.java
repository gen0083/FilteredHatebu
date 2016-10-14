package jp.gcreate.product.filteredhatebu.util;

import android.content.Context;

import com.facebook.stetho.Stetho;

/**
 * Copyright 2016 G-CREATE
 */

public class StethoWrapper {
    private final Context context;

    public StethoWrapper(Context context) {
        this.context = context;
    }

    public void install() {
        Stetho.initializeWithDefaults(context);
    }
}
