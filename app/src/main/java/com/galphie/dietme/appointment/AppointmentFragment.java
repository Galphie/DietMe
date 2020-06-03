package com.galphie.dietme.appointment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.galphie.dietme.R;
import com.galphie.dietme.Utils;
import com.galphie.dietme.adapters.AppointmentListAdapter;
import com.galphie.dietme.instantiable.Appointment;
import com.galphie.dietme.instantiable.Signing;
import com.galphie.dietme.instantiable.User;
import com.galphie.dietme.patients.PatientInfoActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Objects;

public class AppointmentFragment extends Fragment implements ValueEventListener, AppointmentListAdapter.OnAppointmentClickListener {

    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference appointmentsReference = database.getReference().child("Citas");
    private DatabaseReference usersReference = database.getReference().child("Usuario");

    private User currentUser;

    private ArrayList<User> patients = new ArrayList<>();
    private ArrayList<Signing> appointments = new ArrayList<>();
    private RecyclerView recyclerView;

    public AppointmentFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            currentUser = getArguments().getParcelable("currentUser");
        }
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Bundle homeBd = new Bundle();
                homeBd.putParcelable("currentUser", currentUser);
                Navigation
                        .findNavController(Objects.requireNonNull(getActivity()), R.id.nav_host_fragment)
                        .navigate(R.id.homeFragment, homeBd);
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_appointment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        usersReference.addValueEventListener(this);

        CalendarView calendarView = view.findViewById(R.id.appointments_calendar_view);
        recyclerView = view.findViewById(R.id.appointments_recycler_view);
        CardView currentDateCard = view.findViewById(R.id.current_date_card);
        TextView currentDateText = view.findViewById(R.id.currentDateText);
        TextView noAppointments = view.findViewById(R.id.no_appointments);

        initRecyclerView();
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                currentDateCard.setVisibility(View.VISIBLE);
                currentDateText.setText(String.valueOf(getTextDate(month, dayOfMonth)));
                String completeDate = getCompleteDate(year, month, dayOfMonth);
                DatabaseReference selectedDayReference = appointmentsReference.child(completeDate);
                selectedDayReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        appointments.clear();
                        noAppointments.setVisibility(View.INVISIBLE);
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            Appointment apptm = new Appointment(ds.getValue(Appointment.class).getPatientId(),
                                    Objects.requireNonNull(ds.getValue(Appointment.class)).getTime(),
                                    Objects.requireNonNull(ds.getValue(Appointment.class)).isPicked());
                            if (Objects.requireNonNull(apptm).isPicked()) {
                                for (int i = 0; i < patients.size(); i++) {
                                    if (Objects.requireNonNull(Utils.MD5(patients.get(i).getEmail())).substring(0, 6).toUpperCase().equals(apptm.getPatientId())) {
                                        appointments.add(new Signing(patients.get(i), apptm));
                                    }
                                }
                            }
                        }
                        if (appointments.size() == 0) {
                            noAppointments.setVisibility(View.VISIBLE);
                        } else {
                            boolean isPicked = false;
                            for (int i = 0; i < appointments.size(); i++) {
                                if (appointments.get(i).getAppointment().isPicked()) {
                                    isPicked = true;
                                }
                            }
                            if (isPicked) {
                                noAppointments.setVisibility(View.INVISIBLE);
                            } else {
                                noAppointments.setVisibility(View.VISIBLE);
                            }
                        }
                        Objects.requireNonNull(recyclerView.getAdapter()).notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });
    }

    private String getTextDate(int month, int dayOfMonth) {
        String textDate = String.valueOf(dayOfMonth);
        switch (month) {
            case 0:
                textDate = textDate + " de enero";
                break;
            case 1:
                textDate = textDate + " de febrero";
                break;
            case 2:
                textDate = textDate + " de marzo";
                break;
            case 3:
                textDate = textDate + " de abril";
                break;
            case 4:
                textDate = textDate + " de mayo";
                break;
            case 5:
                textDate = textDate + " de junio";
                break;
            case 6:
                textDate = textDate + " de julio";
                break;
            case 7:
                textDate = textDate + " de agosto";
                break;
            case 8:
                textDate = textDate + " de septiembre";
                break;
            case 9:
                textDate = textDate + " de octubre";
                break;
            case 10:
                textDate = textDate + " de noviembre";
                break;
            case 11:
                textDate = textDate + " de diciembre";
                break;

        }
        return textDate;
    }

    @NotNull
    private String getCompleteDate(int year, int month, int dayOfMonth) {
        String sYear = String.valueOf(year);
        String sMonth = "";
        String sDay = "";
        if (month + 1 < 10) {
            sMonth = "0" + (month + 1);
        } else {
            sMonth = String.valueOf(month + 1);
        }
        if (dayOfMonth < 10) {
            sDay = "0" + dayOfMonth;
        } else {
            sDay = String.valueOf(dayOfMonth);
        }
        return sYear + "/" + sMonth + "/" + sDay;
    }


    private void initRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        AppointmentListAdapter adapter = new AppointmentListAdapter(appointments, this);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
        patients.clear();
        for (DataSnapshot ds : dataSnapshot.getChildren()) {
            User user = ds.getValue(User.class);
            patients.add(user);
        }
        Collections.sort(patients, (o1, o2) ->
                o1.getEmail().compareToIgnoreCase(o2.getEmail()));
    }

    @Override
    public void onCancelled(@NonNull DatabaseError databaseError) {

    }

    @Override
    public void onAppointmentClick(int position) {
        Intent intent = new Intent(getContext(), PatientInfoActivity.class);
        intent.putExtra("currentUser", currentUser);
        intent.putExtra("patient", appointments.get(position).getUser());
        startActivity(intent);
    }
}
