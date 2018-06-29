package jp.gcreate.product.filteredhatebu.util

import android.content.Context
import com.crashlytics.android.Crashlytics
import io.fabric.sdk.android.Fabric
import jp.gcreate.product.filteredhatebu.di.Scope.AppScope
import javax.inject.Inject

/**
 * Copyright 2018 G-CREATE
 */
@AppScope
class CrashlyticsWrapper @Inject constructor() {
    fun init(context: Context) {
        Fabric.with(context, Crashlytics())
    }
}