package jp.gcreate.sample.daggersandbox.util;

import android.util.Log;

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

        //TODO: post log to crash reporting tool
    }
}
