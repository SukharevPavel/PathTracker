package ru.sukharev.pathtracker.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import ru.sukharev.pathtracker.R;

/**
 * Dialog fragment that allows to set name for saved path
 */
public class PathNamingFragment extends DialogFragment {

    private DialogPathNamingListener mListener;
    private EditText mEditText;

    public PathNamingFragment(){

    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).
                inflate(R.layout.fragment_dialog_path_naming, null);

        mListener = (DialogPathNamingListener) getActivity();
        mEditText = (EditText) view.findViewById(R.id.edit_path_name);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(view);
        builder.setPositiveButton(getString(R.string.dialog_save),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mListener.onGetName(String.valueOf(mEditText.getText()));
                    }
                });
        builder.setNegativeButton(getString(R.string.dialog_cancel), null);
        return builder.show();
    }


    public interface DialogPathNamingListener{

        void onGetName(String name);

    }
}