package ru.sukharev.pathtracker.ui.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;

import ru.sukharev.pathtracker.R;

/**
 * Created by hpc on 12/27/15.
 */
public class EnableGPSDialogFragment extends DialogFragment {


    public EnableGPSDialogFragment() {

    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {


        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(getString(R.string.dialog_enable_GPS));
        builder.setPositiveButton(getString(R.string.dialog_yes),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent callGPSSettingIntent = new Intent(
                                android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(callGPSSettingIntent);
                    }
                });
        builder.setNegativeButton(getString(R.string.dialog_no), null);
        return builder.show();

    }
}
