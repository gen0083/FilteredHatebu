package jp.gcreate.product.filteredhatebu.ext

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity

inline fun <reified T : ViewModel> Fragment.injectViewModel(
    factory: ViewModelProvider.Factory?): T {
    return ViewModelProviders.of(this.activity!!, factory).get(T::class.java)
}

inline fun <reified T : ViewModel>
    FragmentActivity.injectViewModel(factory: ViewModelProvider.Factory?): T {
    return ViewModelProviders.of(this, factory).get(T::class.java)
}
