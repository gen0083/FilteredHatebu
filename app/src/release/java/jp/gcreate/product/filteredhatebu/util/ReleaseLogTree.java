package jp.gcreate.product.filteredhatebu.util;

import android.util.Log;

import com.crashlytics.android.Crashlytics;

import timber.log.Timber;

/**
 * Copyright 2016 G-CREATE
 */

public class ReleaseLogTree extends Timber.Tree {
    @Override
    protected void log(int priority, String tag, String message, Throwable t) {
        if ( priority == Log.VERBOSE || priority == Log.DEBUG) {
            return;
        }

        Crashlytics.log(message);
        if (t != null) {
            Crashlytics.log(priority, tag, message);
            Crashlytics.logException(t);
        }
    }
}
