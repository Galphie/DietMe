package com.galphie.dietme.patients;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.galphie.dietme.R;
import com.galphie.dietme.adapters.AppointmentListAdapter;
import com.galphie.dietme.dialog.NewPatientAppointmentDialog;
import com.galphie.dietme.instantiable.Appointment;
import com.galphie.dietme.instantiable.Signing;
import com.galphie.dietme.instantiable.User;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class PatientAppointmentsFragment extends Fragment implements AppointmentListAdapter.OnAppointmentClickListener {

    private static final String PATIENT_ID = "patientID";
    private static final String PATIENT = "patient";

    private String patientId;
    private User patient;
    private ArrayList<Signing> appointments = new ArrayList<>();
    private FirebaseDatabase database = FirebaseDatabase.getInstance();

    private RecyclerView recyclerView;
    private TextView noAppointmentsText;

    public PatientAppointmentsFragment() {
    }

    public static PatientAppointmentsFragment newInstance(String patientId, User patient) {
        PatientAppointmentsFragment fragment = new PatientAppointmentsFragment();
        Bundle args = new Bundle();
        args.putString(PATIENT_ID, patientId);
        args.putParcelable(PATIENT, patient);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            patientId = getArguments().getString(PATIENT_ID);
            patient = getArguments().getParcelable(PATIENT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_patient_appointments, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        DatabaseReference patientAppointmentsReference = database.getReference().child("Citas/users").child(patientId);

        recyclerView = view.findViewById(R.id.patient_appointments_recycler_view);
        noAppointmentsText = view.findViewById(R.id.no_patient_appointments);
        FloatingActionButton addAppointment = view.findViewById(R.id.add_patient_appointment);

        addAppointment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment dialogFragment = new NewPatientAppointmentDialog();
                Bundle bundle = new Bundle();
                bundle.putParcelable("patient", patient);
                dialogFragment.setArguments(bundle);
                dialogFragment.show(getActivity().getSupportFragmentManager(), "New appointment");
            }
        });

        initRecyclerView();
        patientAppointmentsReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                appointments.clear();
                if (dataSnapshot.hasChildren()) {
                    noAppointmentsText.setVisibility(View.GONE);
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        Appointment appointment = ds.getValue(Appointment.class);

                    }
                    recyclerView.getAdapter().notifyDataSetChanged();
                } else {
                    noAppointmentsText.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void initRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        AppointmentListAdapter adapter = new AppointmentListAdapter(appointments, this);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onAppointmentClick(int position) {

    }
}
