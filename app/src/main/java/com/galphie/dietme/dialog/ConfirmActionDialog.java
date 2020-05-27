package com.galphie.dietme.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.galphie.dietme.R;
import com.galphie.dietme.Utils;
import com.galphie.dietme.patients.PatientsFragment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ConfirmActionDialog extends DialogFragment {

    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference usersRef = database.getReference("Usuario");

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
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
                        } else if (mArgs.getString("Type").equals("Delete")) {
                            usersRef.child(mArgs.getString("Object").toUpperCase()).removeValue();

                            dialog.dismiss();
                        }
                    })
                    .setNegativeButton(R.string.cancel, (dialog, id) -> dialog.dismiss());
        }


        return builder.create();
    }


}
