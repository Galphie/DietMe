package com.galphie.dietme.patients;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.galphie.dietme.MainActivity;
import com.galphie.dietme.R;
import com.galphie.dietme.Utils;
import com.galphie.dietme.instantiable.User;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Objects;

public class PatientsFragment extends Fragment implements PatientsListAdapter.OnPatientClickListener {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference usersRef = database.getReference("Usuario");
    private String mParam1;
    private String mParam2;
    private ArrayList<User> patientsList;
    private User currentUser;
    private RecyclerView recyclerView;
    private FloatingActionButton addPatientButton;

    public PatientsFragment() {
    }

    // TODO: Rename and change types and number of parameters
    public static PatientsFragment newInstance(String param1, String param2) {
        PatientsFragment fragment = new PatientsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            patientsList = getArguments().getParcelableArrayList("PatientsList");
            currentUser = getArguments().getParcelable("CurrentUser");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_patients, container, false);

        recyclerView =  view.findViewById(R.id.patients_recycler_view);
        initRecyclerView();
        addPatientButton =  view.findViewById(R.id.add_patient_button);
        addPatientButton.setOnClickListener(v -> Utils.toast(Objects.requireNonNull(getActivity()).getApplicationContext(),
                "Añadiendo paciente (en desarrollo)"));
        return view;
    }

    private void initRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        PatientsListAdapter adapter = new PatientsListAdapter(patientsList, this);
        recyclerView.setAdapter(adapter);
    }


    @Override
    public void onPatientClick(int position) {
        String patientId = Objects.requireNonNull(Utils.MD5(patientsList.get(position).getEmail())).substring(0, 6).toUpperCase();
        Intent intent = new Intent(getContext(), PatientInfoActivity.class);
        intent.putExtra("CurrentUser", currentUser);
        intent.putExtra("PatientID", patientId);
        intent.putExtra("Patient", patientsList.get(position));
        startActivity(intent);
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}
