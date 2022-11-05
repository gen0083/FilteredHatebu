package jp.gcreate.product.filteredhatebu.util

import android.util.Log
import com.google.firebase.crashlytics.FirebaseCrashlytics
import timber.log.Timber

/**
 * Copyright 2016 G-CREATE
 */
class ReleaseLogTree : Timber.Tree() {
    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        if (priority == Log.VERBOSE || priority == Log.DEBUG) {
            return
        }
        FirebaseCrashlytics.getInstance().log(message)
        if (t != null) {
            FirebaseCrashlytics.getInstance().recordException(t)
        }
    }
}