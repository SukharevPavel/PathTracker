package ru.sukharev.pathtracker.ui.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;

import ru.sukharev.pathtracker.R;

/**
 * Fragment that asks user if he really want clear current info about path
 */
public class ClearDialogFragment extends DialogFragment {

    private DialogClearListener mListener;

    public ClearDialogFragment() {

    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        mListener = (DialogClearListener) getActivity();

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(getString(R.string.dialog_is_really_clear));
        builder.setPositiveButton(getString(R.string.dialog_yes),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mListener.onClear();
                    }
                });
        builder.setNegativeButton(getString(R.string.dialog_no), null);
        return builder.show();

    }

    public interface DialogClearListener {

        void onClear();

    }

}
