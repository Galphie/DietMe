package com.galphie.dietme.config;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.galphie.dietme.R;
import com.galphie.dietme.Utils;
import com.galphie.dietme.adapters.DietistAppointmentListAdapter;
import com.galphie.dietme.adapters.PatientAppointmentListAdapter;
import com.galphie.dietme.dialog.ConfirmActionDialog;
import com.galphie.dietme.instantiable.Appointment;
import com.galphie.dietme.instantiable.User;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Objects;

public class AvailabilityManagementFragment extends Fragment implements ValueEventListener, DietistAppointmentListAdapter.OnDietistAppointmentClickListener {
    private static final String CURRENT_USER = "currentUser";
    private FirebaseDatabase database = FirebaseDatabase.getInstance();

    private String dietistId = Utils.MD5("dietista").substring(0, 6).toUpperCase();
    private User currentUser;

    private RecyclerView recyclerView;
    private EditText startDateEdit, endDateEdit;
    private ArrayList<Appointment> dietistApppointments = new ArrayList<>();
    private Button applyButton;

    public AvailabilityManagementFragment() {
    }

    public static AvailabilityManagementFragment newInstance(User currentUser) {
        AvailabilityManagementFragment fragment = new AvailabilityManagementFragment();
        Bundle args = new Bundle();
        args.putParcelable(CURRENT_USER, currentUser);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            currentUser = getArguments().getParcelable(CURRENT_USER);
        }
        DatabaseReference dietistRef = database.getReference().child("Citas/users/" + dietistId);
        dietistRef.addValueEventListener(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_availability_management, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        startDateEdit = view.findViewById(R.id.startDateInput);
        endDateEdit = view.findViewById(R.id.endDateInput);
        applyButton = view.findViewById(R.id.apply_button);

        startDateEdit.setInputType(InputType.TYPE_NULL);
        endDateEdit.setInputType(InputType.TYPE_NULL);

        startDateEdit.setOnClickListener(v -> showDateTimeDialog(startDateEdit));
        endDateEdit.setOnClickListener(v -> showDateTimeDialog(endDateEdit));

        applyButton.setOnClickListener(v -> {
            if (currentUser.isAdmin()) {
                if (checkDates(startDateEdit.getText().toString(), endDateEdit.getText().toString())) {
                    DatabaseReference appointmentsRef = database.getReference()
                            .child("Citas");

                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd/HH:mm");
                    LocalDateTime firstDay = LocalDateTime.parse(startDateEdit.getText().toString(), formatter);
                    LocalDateTime lastDay = LocalDateTime.parse(endDateEdit.getText().toString(), formatter);

                    firstDay = firstDay.minusMinutes(firstDay.getMinute());
                    lastDay = lastDay.minusMinutes(lastDay.getMinute());
                    if (firstDay.isAfter(LocalDateTime.now())) {
                        setAppointmentsPicked(firstDay, lastDay, formatter, appointmentsRef);
                    } else {
                        Snackbar.make(Objects.requireNonNull(getActivity()).findViewById(R.id.availability_fragment_parent), getString(R.string.impossible_to_block_previous_date), BaseTransientBottomBar.LENGTH_LONG)
                                .setBackgroundTint(ResourcesCompat.getColor(getResources(), R.color.colorPrimaryDark, null))
                                .show();
                    }
                } else {
                    Utils.toast(getActivity().getApplicationContext(), getString(R.string.invalid_dates));
                }
            } else {
                Utils.toast(getActivity().getApplicationContext(), getString(R.string.developer_action_only));
            }
        });

        recyclerView = view.findViewById(R.id.dietist_recycler_view);
        initRecyclerView();
    }

    private void initRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        DietistAppointmentListAdapter adapter = new DietistAppointmentListAdapter(dietistApppointments, this);
        recyclerView.setAdapter(adapter);
    }

    private void setAppointmentsPicked(LocalDateTime firstDay, LocalDateTime lastDay,
                                       DateTimeFormatter formatter, DatabaseReference appointmentsRef) {
        long duration = Duration.between(firstDay, lastDay).toHours();
        String stringFirstDay;
        for (int i = 0; i < duration; i++) {
            if (firstDay.isAfter(lastDay)) {
                break;
            }
            stringFirstDay = firstDay.format(formatter);
            Appointment dietistAppointment = new Appointment(dietistId,
                    stringFirstDay.substring(11, 16),
                    stringFirstDay.substring(0, 10).replace("/", "-"),
                    true);
            appointmentsRef.child(stringFirstDay).setValue(dietistAppointment);
            DatabaseReference dietistRef = appointmentsRef.child("users/" + dietistId).child(stringFirstDay.substring(0, 10).replace("/", "-") + "-" + stringFirstDay.substring(11, 16));
            dietistRef.setValue(dietistAppointment);
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
        Snackbar.make(Objects.requireNonNull(getActivity()).findViewById(R.id.availability_fragment_parent), getString(R.string.blocked_appointments), BaseTransientBottomBar.LENGTH_LONG)
                .setBackgroundTint(ResourcesCompat.getColor(getResources(), R.color.colorPrimaryDark, null))
                .show();
    }

    private boolean checkDates(String startDate, String endDate) {
        if (!startDate.equals("") && !endDate.equals("")) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd/HH:mm");
            LocalDateTime localDateStart = LocalDateTime.parse(startDate, formatter);
            LocalDateTime localDateEnd = LocalDateTime.parse(endDate, formatter);
            return !localDateEnd.isBefore(localDateStart);
        } else {
            return false;
        }
    }

    private void showDateTimeDialog(EditText startDateEdit) {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog.OnDateSetListener dateSetListener = (view, year, month, dayOfMonth) -> {
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, month);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            TimePickerDialog.OnTimeSetListener timeSetListener = (view1, hourOfDay, minute) -> {
                if (hourOfDay < 9) {
                    calendar.set(Calendar.HOUR_OF_DAY, 9);
                } else if (hourOfDay > 20) {
                    calendar.set(Calendar.HOUR_OF_DAY, 20);
                } else {
                    calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                }

                calendar.set(Calendar.MINUTE, minute);

                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd/HH:mm");

                startDateEdit.setText(simpleDateFormat.format(calendar.getTime()));
            };

            new TimePickerDialog(getActivity(), timeSetListener,
                    calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show();
        };

        new DatePickerDialog(getActivity(), dateSetListener, calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    @Override
    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
        dietistApppointments.clear();
        if (dataSnapshot.hasChildren()) {
            for (DataSnapshot ds : dataSnapshot.getChildren()) {
                Appointment appt = ds.getValue(Appointment.class);
                String appointmentDate = appt.getDate() + " " + appt.getTime();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
                LocalDateTime completeDate = LocalDateTime.parse(appointmentDate, formatter);
                if (completeDate.isAfter(LocalDateTime.now())) {
                    dietistApppointments.add(appt);
                }
            }
            Collections.sort(dietistApppointments, (o1, o2) -> o1.getDate().compareToIgnoreCase(o2.getDate()));
            recyclerView.getAdapter().notifyDataSetChanged();
        }
    }

    @Override
    public void onCancelled(@NonNull DatabaseError databaseError) {

    }

    @Override
    public void onDietistAppointmentClick(int position) {
        DialogFragment dialogFragment = new ConfirmActionDialog();
        Bundle bundle = new Bundle();
        bundle.putString("confirm_action_dialog_message", "¿Desear liberar la cita del "
                + PatientAppointmentListAdapter.setDisplayDate(dietistApppointments.get(position).getDate())
                + " a las " + dietistApppointments.get(position).getTime() + "?");
        bundle.putInt("type", ConfirmActionDialog.DELETE_APPOINTMENT_CODE);
        bundle.putParcelable("object", dietistApppointments.get(position));
        bundle.putString("patientId", dietistId);
        dialogFragment.setArguments(bundle);
        dialogFragment.show(Objects.requireNonNull(getActivity()).getSupportFragmentManager(), "Confirm");
    }
}

