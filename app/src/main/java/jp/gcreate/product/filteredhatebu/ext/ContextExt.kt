package jp.gcreate.product.filteredhatebu.ext

import android.content.Context
import jp.gcreate.product.filteredhatebu.CustomApplication
import jp.gcreate.product.filteredhatebu.di.AppComponent

fun Context.getAppComponent(): AppComponent {
    val app = this.applicationContext as CustomApplication
    return app.appComponent
}
