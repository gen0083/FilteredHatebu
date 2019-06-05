package jp.gcreate.product.filteredhatebu.util

import android.content.Context
import com.crashlytics.android.Crashlytics
import io.fabric.sdk.android.Fabric

/**
 * Copyright 2018 G-CREATE
 */
class CrashlyticsWrapper {
    fun init(context: Context) {
        Fabric.with(context, Crashlytics())
    }
}
