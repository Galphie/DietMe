package com.galphie.dietme.patients;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.galphie.dietme.R;
import com.galphie.dietme.User;
import com.galphie.dietme.Utils;

import java.util.ArrayList;

public class PatientsFragment extends Fragment implements PatientsListAdapter.OnPatientClickListener {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;
    private ArrayList<User> patientsList;
    private RecyclerView recyclerView;

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
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_patients, container, false);

        recyclerView = (RecyclerView) view.findViewById(R.id.patients_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        PatientsListAdapter adapter = new PatientsListAdapter(patientsList,this);
        recyclerView.setAdapter(adapter);

        return view;
    }

    @Override
    public void onPatientClick(int position) {
        String patientId = Utils.MD5(patientsList.get(position).getEmail()).substring(0, 6).toUpperCase();
        Intent intent = new Intent(getContext(),PatientInfoActivity.class);
        intent.putExtra("PatientID", patientId);
        intent.putExtra("Patient", (Parcelable) patientsList.get(position));
        startActivity(intent);
    }

    @Override
    public void onPause() {
        super.onPause();
    }
}
