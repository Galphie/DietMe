package com.galphie.dietme.patients;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.MenuItem;
import android.widget.TextView;

import com.galphie.dietme.R;
import com.galphie.dietme.User;
import com.galphie.dietme.config.ConfigListActivity;

public class PatientInfoActivity extends AppCompatActivity {

    private String patientId;
    private User patient;
    private TextView name,id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_info);

        this.patient = getIntent().getExtras().getParcelable("Patient");
        this.patientId = getIntent().getExtras().getString("PatientID");
        name = (TextView) findViewById(R.id.patient_info_name);
        id = (TextView) findViewById(R.id.patient_info_id);

        name.setText(patient.getName());
        id.setText(patientId);


    }
}
