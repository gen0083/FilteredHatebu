package jp.gcreate.product.filteredhatebu.ui.feeddetail

import android.app.Dialog
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.widget.ArrayAdapter
import dagger.android.support.DaggerAppCompatDialogFragment
import jp.gcreate.product.filteredhatebu.R
import jp.gcreate.product.filteredhatebu.di.ViewModelProviderFactory
import jp.gcreate.product.filteredhatebu.ui.common.FilterGenerator
import timber.log.Timber
import javax.inject.Inject

class PickFilterDialogFragment : DaggerAppCompatDialogFragment() {
    @Inject lateinit var factory: ViewModelProviderFactory
    private lateinit var vm: FeedDetailViewModel
    
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        vm = ViewModelProviders.of(activity!!, factory)[FeedDetailViewModel::class.java]
        Timber.d("vm current url: ${vm.currentUrl}")
        val url = vm.currentUrl
        val adapter = ArrayAdapter<String>(context, android.R.layout.simple_list_item_1)
        adapter.addAll(
            FilterGenerator.generateFilterCandidate(
                url))
        val builder = AlertDialog.Builder(context!!)
        builder.setTitle(R.string.filter_candidate)
            .setNegativeButton(R.string.cancel) { _, _ -> dismiss()}
            .setAdapter(adapter) { _, position ->
                val selected = adapter.getItem(position)
                Timber.d("selected: $selected")
                vm.addFilter(selected)
            }
        return builder.create()
    }
}
