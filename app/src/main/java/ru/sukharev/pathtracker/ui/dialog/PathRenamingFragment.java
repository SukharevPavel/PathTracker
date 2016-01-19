package ru.sukharev.pathtracker.ui.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import ru.sukharev.pathtracker.R;

/**
 * Dialog fragment that allows to set name for saved path
 */
public class PathRenamingFragment extends DialogFragment {

    public final static String ARG_NAME = "name";
    private DialogPathRenamingListener mListener;
    private EditText mEditText;
    private DateFormat format = SimpleDateFormat.getDateTimeInstance();

    public PathRenamingFragment() {

    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).
                inflate(R.layout.fragment_dialog_path_naming, null);

        mListener = (DialogPathRenamingListener) getTargetFragment();
        mEditText = (EditText) view.findViewById(R.id.edit_path_name);

        mEditText.setText(getArguments().getString(ARG_NAME));

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(view);
        builder.setPositiveButton(getString(R.string.dialog_save),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String name = String.valueOf(mEditText.getText());
                        mListener.onNewName(name);
                    }
                });
        builder.setNegativeButton(getString(R.string.dialog_cancel), null);
        return builder.show();
    }


    public interface DialogPathRenamingListener {

        void onNewName(String name);

    }
}