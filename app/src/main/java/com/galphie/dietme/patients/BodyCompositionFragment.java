package com.galphie.dietme.patients;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.galphie.dietme.R;
import com.galphie.dietme.instantiable.User;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class BodyCompositionFragment extends Fragment {

    private static final String ARG_PARAM1 = "Patient";
    private static final String ARG_PARAM2 = "PatientID";

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference usersRef = database.getReference("Usuario");
    private User patient;
    private String patientId;

    private TextView patata;

    public BodyCompositionFragment() {
        // Required empty public constructor
    }

     static BodyCompositionFragment newInstance(User patient, String patientId) {
        BodyCompositionFragment fragment = new BodyCompositionFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_PARAM1, patient);
        args.putString(ARG_PARAM2, patientId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            patient = getArguments().getParcelable(ARG_PARAM1);
            patientId = getArguments().getString(ARG_PARAM2);

            DatabaseReference patientRef = database.getReference("Usuario/" + patientId);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_body_composition, container, false);


        patata = view.findViewById(R.id.patata);
        patata.setText(patient.getName());
        return view;
    }
}
