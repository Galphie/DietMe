package com.galphie.dietme.patients;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.galphie.dietme.R;
import com.galphie.dietme.User;
import com.galphie.dietme.config.ConfigListActivity;

public class PatientInfoActivity extends AppCompatActivity {

    private String patientId;
    private User patient;
    private TextView name,id;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_info);

        toolbar = (Toolbar) findViewById(R.id.activity_patient_info_toolbar);
        setSupportActionBar(toolbar);
        this.patient = getIntent().getExtras().getParcelable("Patient");
        this.patientId = getIntent().getExtras().getString("PatientID");
        getSupportActionBar().setTitle(patient.getName() + " " + patient.getForenames());
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        name = (TextView) findViewById(R.id.patient_info_name);
        id = (TextView) findViewById(R.id.patient_info_id);

        name.setText(patient.getName());
        id.setText(patientId);


    }
}
