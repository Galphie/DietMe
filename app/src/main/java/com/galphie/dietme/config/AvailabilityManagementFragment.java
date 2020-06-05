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
import androidx.fragment.app.Fragment;

import com.galphie.dietme.R;
import com.galphie.dietme.Utils;
import com.galphie.dietme.instantiable.Appointment;
import com.galphie.dietme.instantiable.User;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;

public class AvailabilityManagementFragment extends Fragment {
    private static final String CURRENT_USER = "currentUser";
    private FirebaseDatabase database = FirebaseDatabase.getInstance();

    private String dietistId = Utils.MD5("dietista").substring(0, 6).toUpperCase();
    private User currentUser;
    private EditText startDateEdit, endDateEdit;
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

        applyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentUser.isAdmin()) {
                    if (checkDates(startDateEdit.getText().toString(), endDateEdit.getText().toString())) {
                        DatabaseReference appointmentsRef = database.getReference()
                                .child("Citas");

                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd/HH:mm");
                        LocalDateTime firstDay = LocalDateTime.parse(startDateEdit.getText().toString(), formatter);
                        LocalDateTime lastDay = LocalDateTime.parse(endDateEdit.getText().toString(), formatter);

                        firstDay = firstDay.minusMinutes(firstDay.getMinute());
                        lastDay = lastDay.minusMinutes(lastDay.getMinute());
                        long duration = Duration.between(firstDay, lastDay).toHours();
                        setAppointmentsPicked(firstDay, lastDay, formatter, appointmentsRef);
                    } else {
                        Utils.toast(getActivity().getApplicationContext(), getString(R.string.invalid_dates));
                    }
                } else {
                    Utils.toast(getActivity().getApplicationContext(), getString(R.string.developer_action_only));
                }
            }
        });
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
            DatabaseReference dietistRef = appointmentsRef.child("users/"+dietistId).child(stringFirstDay.substring(0, 10).replace("/", "-")).child(stringFirstDay.substring(11, 16));
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
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
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
}
