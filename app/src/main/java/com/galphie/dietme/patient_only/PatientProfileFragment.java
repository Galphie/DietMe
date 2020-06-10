package com.galphie.dietme.patient_only;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.galphie.dietme.R;
import com.galphie.dietme.Utils;
import com.galphie.dietme.instantiable.User;
import com.galphie.dietme.patients.PatientInfoActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;


public class PatientProfileFragment extends Fragment implements ValueEventListener {

    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private User patient;
    private String patientId;

    private TextView patientEmailText;
    private TextView patientPhoneText;
    private TextView patientAgeText;
    private TextView patientGenderText;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            this.patient = getArguments().getParcelable("patient");
            this.patientId = getArguments().getString("patientId");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_patient_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        DatabaseReference patientReference = database.getReference().child("Usuario/" + patientId);
        patientReference.addValueEventListener(this);

        patientEmailText = view.findViewById(R.id.profile_patient_email);
        patientPhoneText = view.findViewById(R.id.profile_patient_phone);
        patientAgeText = view.findViewById(R.id.profile_patient_age);
        patientGenderText = view.findViewById(R.id.profile_patient_gender);

        BasicInfoProfileFragment basicInfoProfileFragment = BasicInfoProfileFragment.newInstance(patientId, patient);
        getChildFragmentManager()
                .beginTransaction()
                .add(R.id.profile_container, basicInfoProfileFragment)
                .commit();
    }

    @Override
    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
        patient.setName(Objects.requireNonNull(dataSnapshot.getValue(User.class)).getName());
        patient.setForenames(Objects.requireNonNull(dataSnapshot.getValue(User.class)).getForenames());
        patient.setEmail(Objects.requireNonNull(dataSnapshot.getValue(User.class)).getEmail());
        patient.setBirthdate(Objects.requireNonNull(dataSnapshot.getValue(User.class)).getBirthdate());
        patient.setGender(Objects.requireNonNull(dataSnapshot.getValue(User.class)).getGender());
        patient.setPhone(Objects.requireNonNull(dataSnapshot.getValue(User.class)).getPhone());
        patient.setMeasures(Objects.requireNonNull(dataSnapshot.getValue(User.class)).getMeasures());

        int age = Utils.calculateAge(patient.getBirthdate());
        patientAgeText.setText(String.valueOf(age));
        patientEmailText.setText(patient.getEmail());
        patientPhoneText.setText(PatientInfoActivity.setPhoneFormat(patient.getPhone()));
        if (patient.getGender() == 1) {
            patientGenderText.setText(getString(R.string.female));
        } else if (patient.getGender() == 2) {
            patientGenderText.setText(getString(R.string.male));
        } else {
            patientGenderText.setText(R.string.not_specified);
        }
    }

    @Override
    public void onCancelled(@NonNull DatabaseError databaseError) {

    }
}
