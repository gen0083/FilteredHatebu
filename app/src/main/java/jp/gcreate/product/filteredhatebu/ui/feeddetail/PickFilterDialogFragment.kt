package jp.gcreate.product.filteredhatebu.ui.feeddetail

import android.app.Dialog
import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import jp.gcreate.product.filteredhatebu.R
import jp.gcreate.product.filteredhatebu.ui.common.FilterGenerator
import org.koin.androidx.viewmodel.ext.android.activityViewModel
import timber.log.Timber

class PickFilterDialogFragment : DialogFragment() {
    private val vm: FeedDetailViewModel by activityViewModel()
    
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        Timber.d("vm current url: ${vm.currentUrl}")
        val url = vm.currentUrl
        val adapter = ArrayAdapter<String>(requireContext(), android.R.layout.simple_list_item_1)
        adapter.addAll(
            FilterGenerator.generateFilterCandidate(
                url))
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle(R.string.filter_candidate)
            .setNegativeButton(R.string.cancel) { _, _ -> dismiss()}
            .setAdapter(adapter) { _, position ->
                val selected = adapter.getItem(position) ?: ""
                Timber.d("selected: $selected")
                vm.addFilter(selected)
            }
        return builder.create()
    }
}
