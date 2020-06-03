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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class ConfirmActionDialog extends DialogFragment {

    public static final int CALL_CODE = 112;
    public static final int EMAIL_CODE = 1611;
    public static final int NEW_APPOINTMENT_CODE = 4283;
    public static final int DELETE_PATIENT_CODE = 6661;
    public static final int DELETE_FILE_CODE = 6662;
    public static final int DELETE_POST_CODE = 6663;
    public static final int DELETE_APPOINTMENT_CODE = 6664;
    public static final int RESTART_CODE = 0;

    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private FirebaseDatabase database = FirebaseDatabase.getInstance();

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        Bundle mArgs = getArguments();
        if (mArgs != null) {
            builder.setMessage(mArgs.getString("confirm_action_dialog_message"))
                    .setPositiveButton(getString(R.string.accept), (dialog, id) -> {
                        switch (mArgs.getInt("type")) {
                            case CALL_CODE:
                                Intent call = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + mArgs.getString("Object")));
                                startActivity(call);
                                break;
                            case EMAIL_CODE:
                                Intent email = new Intent(Intent.ACTION_SENDTO,
                                        Uri.fromParts("mailto", mArgs.getString("object"), null));
                                startActivity(email);
                                break;
                            case RESTART_CODE:
                                DatabaseReference appointmentsRef = database.getReference("Citas");
                                String start = "2020/05/25/09:00";
                                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd/HH:mm");
                                LocalDateTime firstDay = LocalDateTime.parse(start, formatter);
                                resetAppointmentsDatabase(appointmentsRef, formatter, firstDay);
                                appointmentsRef.child("users").removeValue();

                                Utils.toast(Objects.requireNonNull(getActivity()).getApplicationContext(), getString(R.string.database_restarted));
                                break;
                            case DELETE_PATIENT_CODE:
                                DatabaseReference usersRef = database.getReference("Usuario");
                                usersRef.child(Objects.requireNonNull(mArgs.getString("object")).toUpperCase()).removeValue();
                                break;
                            case DELETE_FILE_CODE:
                                String patientId = mArgs.getString("patientId");
                                String fileName = mArgs.getString("fileName");
                                String path = mArgs.getString("path");
                                StorageReference fileCloudRef = storage.getReference().child(Objects.requireNonNull(path));
                                DatabaseReference fileDatabaseRef = database.getReference().child("Archivos/users/" + patientId).child(Objects.requireNonNull(Utils.MD5(Objects.requireNonNull(fileName))));
                                fileCloudRef.delete().addOnSuccessListener(aVoid -> fileDatabaseRef.removeValue());
                                break;
                            case DELETE_POST_CODE:
                                String postPublishDate = mArgs.getString("object");
                                database.getReference().child("Publicaciones").child(Objects.requireNonNull(postPublishDate)).removeValue();
                                break;
                            case NEW_APPOINTMENT_CODE:
                                String dayRef = mArgs.getString("dayRef");
                                Appointment newAppointment = mArgs.getParcelable("object");
                                newAppointment.setDate(dayRef.replace("/", "-"));
                                DatabaseReference newAppointmentRef = database.getReference().child("Citas/" + dayRef).child(newAppointment.getTime());
                                DatabaseReference userAppointmentsRef = database.getReference().child("Citas/users/" + newAppointment.getPatientId()).child(dayRef.replace("/", "-"));

                                newAppointmentRef.setValue(newAppointment);
                                userAppointmentsRef.setValue(newAppointment);
                                if (mArgs.getBoolean("edit")) {
                                    Appointment editedAppointment = mArgs.getParcelable("appointmentToEdit");
                                    DatabaseReference userAppointmentToReplaceRef = database.getReference()
                                            .child("Citas/users")
                                            .child(editedAppointment.getPatientId() + "/" + editedAppointment.getDate());
                                    DatabaseReference appointmentToReplaceRef = database.getReference()
                                            .child("Citas").child(editedAppointment.getDate().replace("-", "/"))
                                            .child(editedAppointment.getTime());
                                    if (!newAppointment.getDate().equals(editedAppointment.getDate())) {
                                        userAppointmentToReplaceRef.removeValue();
                                    }
                                    appointmentToReplaceRef.setValue(new Appointment(editedAppointment.getTime(), false));
                                }
                                break;
                            case DELETE_APPOINTMENT_CODE:
                                Appointment deleteAppointment = mArgs.getParcelable("object");
                                String patient = mArgs.getString("patientId");
                                DatabaseReference userAppointmentToDeleteRef = database.getReference()
                                        .child("Citas/users")
                                        .child(patient + "/" + deleteAppointment.getDate());
                                DatabaseReference appointmentToDeleteRef = database.getReference()
                                        .child("Citas").child(deleteAppointment.getDate().replace("-", "/"))
                                        .child(deleteAppointment.getTime());
                                userAppointmentToDeleteRef.removeValue();
                                appointmentToDeleteRef.setValue(new Appointment(deleteAppointment.getTime(), false));
                                break;
                        }
                        dialog.dismiss();
                    }).setNegativeButton(R.string.cancel, (dialog, id) -> dialog.dismiss());
        }
        return builder.create();
    }

    private void resetAppointmentsDatabase(DatabaseReference appointmentsRef, DateTimeFormatter formatter, LocalDateTime firstDay) {
        String stringFirstDay;
        for (int i = 0; i < 5000; i++) {
            stringFirstDay = firstDay.format(formatter);
            Appointment emptyAppointment = new Appointment(stringFirstDay.substring(11, 16), false);
            appointmentsRef.child(stringFirstDay).setValue(emptyAppointment);
            if (firstDay.getHour() == 13) {
                firstDay = firstDay.plusHours(2);
            }
            if (firstDay.getHour() == 19) {
                firstDay = firstDay.plusHours(13);
            }
            if (firstDay.getDayOfWeek() == DayOfWeek.SATURDAY) {
                firstDay = firstDay.plusDays(2);
            }
            firstDay = firstDay.plusHours(1);
        }
    }

}
