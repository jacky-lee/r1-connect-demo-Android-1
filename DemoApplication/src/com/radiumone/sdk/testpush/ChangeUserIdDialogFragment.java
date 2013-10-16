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

public class ChangeUserIdDialogFragment extends DialogFragment {

    private WeakReference<OnUserIdChangedListener> listenerRef;

    public static ChangeUserIdDialogFragment newInstance(OnUserIdChangedListener listener){
        ChangeUserIdDialogFragment fragment = new ChangeUserIdDialogFragment();
        fragment.setOnCreatedListener(listener);
        return fragment;
    }

    private void setOnCreatedListener(OnUserIdChangedListener listener) {
        this.listenerRef = new WeakReference<OnUserIdChangedListener>(listener);
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final EditText editText = new EditText(getActivity());
        int maxLength = 128;
        editText.setFilters(new InputFilter[] {new InputFilter.LengthFilter(maxLength)});
        editText.setHint("user Id");
        builder.setTitle("Change User ID").setView(editText)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if ( listenerRef != null ){
                            OnUserIdChangedListener listener = listenerRef.get();
                            if ( listener != null && editText.getText() != null ){
                                listener.onUserIdChanged(editText.getText().toString());
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

    public interface OnUserIdChangedListener {
        public void onUserIdChanged(String userId);
    }
}
