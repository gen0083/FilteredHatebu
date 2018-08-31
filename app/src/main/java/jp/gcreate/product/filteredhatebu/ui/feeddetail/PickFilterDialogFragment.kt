package jp.gcreate.product.filteredhatebu.ui.feeddetail

import android.app.Dialog
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog
import android.widget.ArrayAdapter
import jp.gcreate.product.filteredhatebu.R
import jp.gcreate.product.filteredhatebu.ui.common.FilterGenerator
import org.koin.android.architecture.ext.sharedViewModel
import timber.log.Timber

class PickFilterDialogFragment : DialogFragment() {
    private val vm: FeedDetailViewModel by sharedViewModel()
    
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
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
