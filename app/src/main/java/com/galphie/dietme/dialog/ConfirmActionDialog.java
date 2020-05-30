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
import com.galphie.dietme.instantiable.Appointment;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ConfirmActionDialog extends DialogFragment {

    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference sharedFilesRef = database.getReference("Archivos").child("users");
    private DatabaseReference usersRef = database.getReference("Usuario");
    private DatabaseReference appointmentsRef = database.getReference("Citas");

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
                                    "mailto", mArgs.getString("Object"), null));
                            startActivity(intent);
                            dialog.dismiss();
                        } else if (mArgs.getString("Type").equals("Delete")) {
                            usersRef.child(mArgs.getString("Object").toUpperCase()).removeValue();
                            sharedFilesRef.child(mArgs.getString("Object").toUpperCase()).removeValue();
                            dialog.dismiss();
                        } else if (mArgs.getString("Type").equals("Restart")) {
                            String start = "2020/05/25/09:00";
                            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd/HH:mm");
                            LocalDateTime firstDay = LocalDateTime.parse(start, formatter);
                            String stringFirstDay;
                            Appointment emptyAppointment = new Appointment(false);
                            for (int i = 0; i < 1000; i++) {
                                stringFirstDay = firstDay.format(formatter);
                                appointmentsRef.child(stringFirstDay).setValue(emptyAppointment);
                                if(firstDay.getHour()==18){
                                    firstDay = firstDay.plusHours(14);
                                }
                                if (firstDay.getDayOfWeek() == DayOfWeek.SATURDAY) {
                                    firstDay = firstDay.plusDays(2);
                                }
                                firstDay = firstDay.plusHours(1);
                            }
                            Utils.toast(getActivity().getApplicationContext(),getString(R.string.database_restarted));
                        }
                    })
                    .setNegativeButton(R.string.cancel, (dialog, id) -> dialog.dismiss());
        }


        return builder.create();
    }


}
