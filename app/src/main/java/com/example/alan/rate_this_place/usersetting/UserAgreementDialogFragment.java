package com.example.alan.rate_this_place.usersetting;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import com.example.alan.rate_this_place.R;
import com.example.alan.rate_this_place.pasivedatacollection.SensorListenerService;

/**
 * Created by Xue Fei on 3/6/2015.
 */
public class UserAgreementDialogFragment extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.user_agreement)
                .setPositiveButton(R.string.agree, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // FIRE ZE MISSILES!
                        getActivity().getSharedPreferences("PREFERENCE", getActivity().MODE_PRIVATE)
                                .edit()
                                .putBoolean("DoesUserAgree", true)
                                .apply();

                        Intent startServiceIntent = new Intent(getActivity(), SensorListenerService.class);
                        getActivity().startService(startServiceIntent);

                    }
                })
                .setNegativeButton(R.string.disagree, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                        getActivity().getSharedPreferences("PREFERENCE", getActivity().MODE_PRIVATE)
                                .edit()
                                .putBoolean("DoesUserAgree", false)
                                .apply();

                        getActivity().finish();
                        System.exit(0);
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }
}
