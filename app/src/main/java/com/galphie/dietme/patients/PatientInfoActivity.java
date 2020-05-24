package com.galphie.dietme.patients;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import com.galphie.dietme.R;
import com.galphie.dietme.Utils;
import com.galphie.dietme.instantiable.User;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class PatientInfoActivity extends AppCompatActivity {

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference usersRef = database.getReference("Usuario");
    private CardView emailCardView, phoneCardView, ageGenderCardView, idCardView;
    private TextView patientEmailText, patientPhoneText, patientAgeText,
            patientGenderText, patientIdText;
    private String patientId;
    private User patient;

    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_info);

        this.patient = getIntent().getExtras().getParcelable("Patient");
        this.patientId = getIntent().getExtras().getString("PatientID");

        emailCardView = (CardView) findViewById(R.id.emailCardView);
        phoneCardView = (CardView) findViewById(R.id.phoneCardView);
        ageGenderCardView = (CardView) findViewById(R.id.age_genderCardView);
        idCardView = (CardView) findViewById(R.id.idCardView);

        patientEmailText = (TextView) findViewById(R.id.patient_email);
        patientPhoneText = (TextView) findViewById(R.id.patient_phone);
        patientAgeText = (TextView) findViewById(R.id.patient_age);
        patientGenderText = (TextView) findViewById(R.id.patient_gender);
        patientIdText = (TextView) findViewById(R.id.patient_id);
        //TODO: Bloquear la información
        patientEmailText.setText(patient.getEmail());
        patientPhoneText.setText(patient.getPhone());
        int age = Utils.calculateAge(patient.getBirthdate());
        patientAgeText.setText(String.valueOf(age));
        if (patient.getGender() == 1) {
            patientGenderText.setText(getString(R.string.female));
        } else {
            patientGenderText.setText(getString(R.string.male));
        }
        patientIdText.setText(patientId);
        toolbar = (Toolbar) findViewById(R.id.activity_patient_info_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(patient.getName() + " " + patient.getForenames());
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        DatabaseReference patientRef = database.getReference("Usuario/" + patientId);

    }
}
