package com.galphie.dietme.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.galphie.dietme.R;
import com.galphie.dietme.Utils;
import com.galphie.dietme.config.AppointmentsManagementFragment;
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
                                Intent call = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + mArgs.getString("object")));
                                startActivity(call);
                                break;
                            case EMAIL_CODE:
                                Intent email = new Intent(Intent.ACTION_SENDTO,
                                        Uri.fromParts("mailto", mArgs.getString("object"), null));
                                startActivity(email);
                                break;
                            case RESTART_CODE:
                                AppointmentsManagementFragment.progressBar.setVisibility(View.VISIBLE);
                                DatabaseReference appointmentsRef = database.getReference("Citas");
                                String start = "2020/05/25/09:00";
                                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd/HH:mm");
                                LocalDateTime firstDay = LocalDateTime.parse(start, formatter);
                                getParentFragmentManager().popBackStack();
                                appointmentsRef.child("users").removeValue();
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        appointmentsRef.removeValue().addOnSuccessListener(aVoid1 -> resetAppointmentsDatabase(appointmentsRef, formatter, firstDay));
                                    }
                                }, 3000);
                                Utils.toast(Objects.requireNonNull(getActivity()).getApplicationContext(), getString(R.string.restarting_database));
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
                                database.getReference()
                                        .child("Publicaciones")
                                        .child(Objects.requireNonNull(postPublishDate))
                                        .removeValue();
                                break;
                            case NEW_APPOINTMENT_CODE:
                                if (mArgs.getBoolean("edit")) {
                                    Appointment editedAppointment = mArgs.getParcelable("appointmentToEdit");
                                    DatabaseReference userAppointmentToReplaceRef = database.getReference()
                                            .child("Citas/users")
                                            .child(editedAppointment.getPatientId())
                                            .child(editedAppointment.getDate() + "-" + editedAppointment.getTime());
                                    userAppointmentToReplaceRef.removeValue();
                                }
                                String dayRef = mArgs.getString("dayRef");
                                Appointment newAppointment = mArgs.getParcelable("object");
                                newAppointment.setDate(dayRef.replace("/", "-"));
                                DatabaseReference newAppointmentRef = database.getReference()
                                        .child("Citas/" + dayRef)
                                        .child(newAppointment.getTime());
                                DatabaseReference userAppointmentsRef = database.getReference()
                                        .child("Citas/users/" + newAppointment.getPatientId())
                                        .child(dayRef.replace("/", "-") + "-" + newAppointment.getTime());

                                newAppointmentRef.setValue(newAppointment);
                                userAppointmentsRef.setValue(newAppointment);

                                getActivity().finish();
                                break;
                            case DELETE_APPOINTMENT_CODE:
                                Appointment deleteAppointment = mArgs.getParcelable("object");
                                Appointment emptyAppointment = new Appointment(null, deleteAppointment.getTime(), deleteAppointment.getDate(), false);
                                String patient = mArgs.getString("patientId");
                                DatabaseReference userAppointmentToDeleteRef;
                                userAppointmentToDeleteRef = database.getReference()
                                        .child("Citas/users")
                                        .child(patient + "/" + deleteAppointment.getDate() + "-" + deleteAppointment.getTime());
                                userAppointmentToDeleteRef.removeValue();
                                DatabaseReference appointmentToReplaceRef = database.getReference()
                                        .child("Citas")
                                        .child(deleteAppointment.getDate().replace("-", "/"))
                                        .child(deleteAppointment.getTime());
                                appointmentToReplaceRef.setValue(emptyAppointment);
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
            Appointment emptyAppointment = new Appointment(null, stringFirstDay.substring(11, 16), stringFirstDay.substring(0, 10).replace("/", "-"), false);
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
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                AppointmentsManagementFragment.progressBar.setVisibility(View.INVISIBLE);
            }
        }, 4000);
    }

}
