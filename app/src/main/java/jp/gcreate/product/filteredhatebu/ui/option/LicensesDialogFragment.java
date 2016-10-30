package jp.gcreate.product.filteredhatebu.ui.option;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.webkit.WebView;

import jp.gcreate.product.filteredhatebu.R;

/**
 * Copyright 2016 G-CREATE
 */

public class LicensesDialogFragment extends DialogFragment {
    private static final String LICENSE_URL = "file:///android_asset/licenses.html";

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Context             context = getActivity();
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        WebView             v       = new WebView(context);
        v.loadUrl(LICENSE_URL);
        builder.setTitle(R.string.licenses)
               .setView(v)
               .setNeutralButton(R.string.close, new DialogInterface.OnClickListener() {
                   @Override
                   public void onClick(DialogInterface dialogInterface, int i) {
                       dismiss();
                   }
               });
        return builder.create();
    }
}
