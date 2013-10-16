package com.radiumone.sdk.testpush;

import android.R;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.InputFilter;
import android.widget.EditText;

import java.lang.ref.WeakReference;

public class AddTagDialogFragment extends DialogFragment {

    private WeakReference<OnTagCreatedListener> listenerRef;

    public static AddTagDialogFragment newInstance(OnTagCreatedListener listener){
        AddTagDialogFragment fragment = new AddTagDialogFragment();
        fragment.setOnCreatedListener(listener);
        return fragment;
    }

    private void setOnCreatedListener(OnTagCreatedListener listener) {
        this.listenerRef = new WeakReference<OnTagCreatedListener>(listener);
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final EditText editText = new EditText(getActivity());
        int maxLength = 128;
        editText.setFilters(new InputFilter[] {new InputFilter.LengthFilter(maxLength)});
        editText.setHint("tag (128 max)");
        builder.setTitle("Create Tag").setView(editText)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if ( listenerRef != null ){
                            OnTagCreatedListener listener = listenerRef.get();
                            if ( listener != null && editText.getText() != null ){
                                listener.onTagCreated(editText.getText().toString());
                            }
                        }
                        dialog.dismiss();
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        return builder.create();
    }

    public interface OnTagCreatedListener{
        public void onTagCreated(String tag);
    }
}
