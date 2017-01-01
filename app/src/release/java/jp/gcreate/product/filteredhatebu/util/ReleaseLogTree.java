package jp.gcreate.product.filteredhatebu.util;

import android.util.Log;

import com.google.firebase.crash.FirebaseCrash;

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

        FirebaseCrash.log(message);
        if (t != null) {
            FirebaseCrash.logcat(priority, tag, message);
            FirebaseCrash.report(t);
        }
    }
}
