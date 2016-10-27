package jp.gcreate.product.filteredhatebu.ui.feeddetail;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.widget.ArrayAdapter;

import jp.gcreate.product.filteredhatebu.R;
import jp.gcreate.product.filteredhatebu.ui.common.FilterGenerator;
import timber.log.Timber;

/**
 * Copyright 2016 G-CREATE
 */

public class SelectFilterDialogFragment extends DialogFragment {
    private static final String EXTRA_URI_KEY = "uri_key";
    private Callback callback;

    public static SelectFilterDialogFragment newInstance(String uri) {
        Bundle args = new Bundle();
        args.putString(EXTRA_URI_KEY, uri);
        SelectFilterDialogFragment f = new SelectFilterDialogFragment();
        f.setArguments(args);
        return f;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof Callback) {
            callback = (Callback) context;
        } else {
            throw new RuntimeException(
                    "Activity " + this + " called is must implement Callback interface.");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        callback = null;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String              uri     = getArguments().getString(EXTRA_URI_KEY);
        Timber.d("%s onCreateDialog args uri:%s", this, uri);
        final ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1);
        adapter.addAll(FilterGenerator.generateFilterCandidate(uri));
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.filter_candidate)
               .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                   @Override
                   public void onClick(DialogInterface dialog, int which) {
                       if (callback != null) {
                           callback.onCanceled();
                       }
                   }
               })
                .setAdapter(adapter, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String selected = adapter.getItem(which);
                        Timber.d("%s in ListAdapter onClick:%d %s", SelectFilterDialogFragment.this, which, selected);
                        if (callback != null) {
                            callback.onSelected(selected);
                        }
                    }
                });
        return builder.create();
    }

    public interface Callback {
        void onCanceled();

        void onSelected(String selected);
    }
}
