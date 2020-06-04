package com.galphie.dietme.dialog;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CalendarView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.galphie.dietme.R;
import com.galphie.dietme.Utils;
import com.galphie.dietme.adapters.AppointmentListAdapter;
import com.galphie.dietme.appointment.AppointmentFragment;
import com.galphie.dietme.instantiable.Appointment;
import com.galphie.dietme.instantiable.Signing;
import com.galphie.dietme.instantiable.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Objects;

public class NewPatientAppointmentDialog extends DialogFragment implements AppointmentListAdapter.OnAppointmentClickListener {
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference appointmentsReference = database.getReference().child("Citas");
    private String dayString;

    private ArrayList<Signing> appointments = new ArrayList<>();
    private User patient;

    private RecyclerView recyclerView;
    private TextView noFreeAppointmentsText, currentDateText;
    private CardView currentDateCard;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(Objects.requireNonNull(getActivity()));
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        @SuppressLint("InflateParams") View view = inflater.inflate(R.layout.new_patient_appointment_dialog, null);

        Bundle mArgs = getArguments();
        patient = Objects.requireNonNull(mArgs).getParcelable("patient");

        recyclerView = view.findViewById(R.id.new_appointment_recycler_view);
        CalendarView calendarView = view.findViewById(R.id.new_appointment_calendar_view);
        noFreeAppointmentsText = view.findViewById(R.id.no_free_appointments);
        currentDateText = view.findViewById(R.id.new_appointment_current_date_text);
        currentDateCard = view.findViewById(R.id.new_appointment_current_date_card);

        initRecyclerView();
        calendarView.setOnDateChangeListener((view1, year, month, dayOfMonth) -> {
            recyclerView.setVisibility(View.VISIBLE);
            currentDateCard.setVisibility(View.VISIBLE);
            currentDateText.setText(String.valueOf(AppointmentFragment.getTextDate(month, dayOfMonth)));
            String completeDate = AppointmentFragment.getCompleteDate(year, month, dayOfMonth);
            DatabaseReference selectedDayReference = appointmentsReference.child(completeDate);
            dayString = completeDate;
            selectedDayReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    appointments.clear();
                    noFreeAppointmentsText.setVisibility(View.INVISIBLE);
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        Appointment apptm = new Appointment(ds.getValue(Appointment.class).getPatientId(),
                                Objects.requireNonNull(ds.getValue(Appointment.class)).getTime(),
                                Objects.requireNonNull(ds.getValue(Appointment.class)).getDate(),
                                Objects.requireNonNull(ds.getValue(Appointment.class)).isPicked());
                        patient.setName("");
                        patient.setForenames("");
                        if (!Objects.requireNonNull(apptm).isPicked()) {
                            String compareDate = apptm.getDate() + " " + apptm.getTime() + ":00";
                            LocalDateTime compareLocalDateTime = Utils.stringToLocalDateTime(compareDate);
                            if (!compareLocalDateTime.isBefore(LocalDateTime.now())) {
                                appointments.add(new Signing(patient, apptm));
                            }
                        }
                    }
                    if (appointments.size() == 0) {
                        noFreeAppointmentsText.setVisibility(View.VISIBLE);
                        LocalDate date = Utils.stringToLocalDate(completeDate.replace("/", "-"));
                        if (date.isBefore(LocalDate.now())) {
                            noFreeAppointmentsText.setText(getString(R.string.finalized_day));
                        } else {
                            noFreeAppointmentsText.setText(getString(R.string.no_free_appointments_found));
                        }
                    } else {
                        boolean isPicked = true;
                        for (int i = 0; i < appointments.size(); i++) {
                            if (!appointments.get(i).getAppointment().isPicked()) {
                                isPicked = false;
                            }
                        }
                        if (!isPicked) {
                            noFreeAppointmentsText.setVisibility(View.INVISIBLE);
                        } else {
                            noFreeAppointmentsText.setVisibility(View.VISIBLE);
                            LocalDate date = Utils.stringToLocalDate(completeDate.replace("/", "-"));
                            if (date.isBefore(LocalDate.now())) {
                                noFreeAppointmentsText.setText(getString(R.string.finalized_day));
                            } else {
                                noFreeAppointmentsText.setText(getString(R.string.no_free_appointments_found));
                            }
                        }
                    }
                    Objects.requireNonNull(recyclerView.getAdapter()).notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        });

        builder.setView(view)
                .setNegativeButton(getText(R.string.cancel), (dialog, which) -> dismiss());

        return builder.create();
    }

    private void initRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        AppointmentListAdapter adapter = new AppointmentListAdapter(appointments, this);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onAppointmentClick(int position) {
        Appointment newAppointment = appointments.get(position).getAppointment();
        newAppointment.setPicked(true);
        newAppointment.setPatientId(Objects.requireNonNull(Utils.MD5(patient.getEmail())).substring(0, 6).toUpperCase());


        DialogFragment dialogFragment = new ConfirmActionDialog();
        Bundle bundle = new Bundle();
        bundle.putString("confirm_action_dialog_message", "¿Pedir cita a las " + newAppointment.getTime() + "?");
        bundle.putInt("type", ConfirmActionDialog.NEW_APPOINTMENT_CODE);
        bundle.putString("dayRef", dayString);
        bundle.putParcelable("object", newAppointment);
        if (Objects.requireNonNull(getArguments()).getBoolean("edit")) {
            bundle.putBoolean("edit", true);
            bundle.putParcelable("appointmentToEdit", getArguments().getParcelable("appointmentToEdit"));
        }
        dialogFragment.setArguments(bundle);
        dialogFragment.show(Objects.requireNonNull(getActivity()).getSupportFragmentManager(), "Confirm");

        dismiss();
    }
}
