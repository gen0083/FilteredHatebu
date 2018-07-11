package jp.gcreate.product.filteredhatebu.ui.common

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.widget.ArrayAdapter
import jp.gcreate.product.filteredhatebu.R
import timber.log.Timber

class PickFilterDialogFragment : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val url = arguments?.getString(KEY_URL) ?: throw IllegalArgumentException("must pass url")
        val adapter = ArrayAdapter<String>(context, android.R.layout.simple_list_item_1)
        adapter.addAll(FilterGenerator.generateFilterCandidate(url))
        val builder = AlertDialog.Builder(context!!)
        builder.setTitle(R.string.filter_candidate)
            .setNegativeButton(R.string.cancel) { _, _ -> dismiss()}
            .setAdapter(adapter) { _, position ->
                val selected = adapter.getItem(position)
                val result = Intent().apply {
                    putExtra(Intent.EXTRA_TEXT, selected)
                }
                Timber.d("return result[$result] to target:$targetFragment")
                targetFragment?.onActivityResult(REQUEST_CODE, Activity.RESULT_OK, result)
            }
        return builder.create()
    }
    
    companion object {
        const val KEY_URL = "url"
        const val REQUEST_CODE = 1
        
        @JvmStatic fun createDialog(fragment: Fragment, url: String): PickFilterDialogFragment {
            return PickFilterDialogFragment().apply {
                arguments = Bundle().apply {
                    putString(KEY_URL, url)
                }
                setTargetFragment(fragment, REQUEST_CODE)
            }
        }
    }
}
