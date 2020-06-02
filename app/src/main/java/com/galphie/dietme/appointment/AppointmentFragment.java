package com.galphie.dietme.appointment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class AppointmentFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "currentUser";

    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference appointmentsReference = database.getReference().child("Citas");

    private String mParam1;
    private User currentUser;

    private ArrayList<Signing> appointments = new ArrayList<>();
    private CalendarView calendarView;
    private RecyclerView recyclerView;

    public AppointmentFragment() {
    }

    public static AppointmentFragment newInstance(String param1, User currentUser) {
        AppointmentFragment fragment = new AppointmentFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putParcelable(ARG_PARAM2, currentUser);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            currentUser = getArguments().getParcelable("currentUser");
        }
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Bundle homeBd = new Bundle();
                homeBd.putParcelable("currentUser", currentUser);
                Navigation
                        .findNavController(getActivity(), R.id.nav_host_fragment)
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

        calendarView = view.findViewById(R.id.appointments_calendar_view);
        recyclerView = view.findViewById(R.id.appointments_recycler_view);
        TextView noAppointments = view.findViewById(R.id.no_appointments);

        initRecyclerView();

        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                String completeDate = getCompleteDate(year, month, dayOfMonth);
                Utils.toast(getActivity().getApplicationContext(), completeDate);
                DatabaseReference selectedDayReference = appointmentsReference.child(completeDate);
                selectedDayReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        appointments.clear();
                        noAppointments.setVisibility(View.INVISIBLE);
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            Appointment apptm = ds.getValue(Appointment.class);
                            if (apptm.isPicked()) {
                                appointments.add(new Signing(currentUser, apptm));
                            }
                        }
                        if(appointments.size() == 0) {
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
                        recyclerView.getAdapter().notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });
    }

    @NotNull
    private String getCompleteDate(int year, int month, int dayOfMonth) {
        String sYear = String.valueOf(year);
        String sMonth = "";
        String sDay = "";
        if (month + 1 < 10) {
            sMonth = "0" + String.valueOf(month + 1);
        } else {
            sMonth = String.valueOf(month + 1);
        }
        if (dayOfMonth < 10) {
            sDay = "0" + String.valueOf(dayOfMonth);
        } else {
            sDay = String.valueOf(dayOfMonth);
        }
        return sYear + "/" + sMonth + "/" + sDay;
    }


    private void initRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        AppointmentListAdapter adapter = new AppointmentListAdapter(appointments);
        recyclerView.setAdapter(adapter);
    }
}
