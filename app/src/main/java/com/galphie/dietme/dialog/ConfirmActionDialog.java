package com.galphie.dietme.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.galphie.dietme.R;

public class ConfirmActionDialog extends DialogFragment {

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        Bundle mArgs = getArguments();
        if (mArgs != null) {
            builder.setMessage(mArgs.getString("Message"))
                    .setPositiveButton(getString(R.string.accept), (dialog, id) -> {
                        if (mArgs.getString("Type").equals("Call")) {
                            Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + mArgs.getString("Object")));
                            startActivity(intent);
                            dialog.dismiss();
                        } else if (mArgs.getString("Type").equals("Email")) {
                            Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                                    "mailto",mArgs.getString("Object"), null));
                            startActivity(intent);
                            dialog.dismiss();
                        }
                    })
                    .setNegativeButton(R.string.cancel, (dialog, id) -> dialog.dismiss());
        }


        return builder.create();
    }


}
