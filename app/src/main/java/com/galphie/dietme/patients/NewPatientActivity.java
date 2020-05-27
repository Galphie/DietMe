package com.galphie.dietme.patients;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.galphie.dietme.R;
import com.galphie.dietme.Utils;
import com.galphie.dietme.instantiable.Measures;
import com.galphie.dietme.instantiable.User;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;

public class NewPatientActivity extends AppCompatActivity {

    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference usersRef = database.getReference("Usuario");
    private Measures measures = new Measures(true);
    private String password = null;

    private TextView newPatientText;
    private Button createPatientButton;
    private CheckBox checkIsAdmin;
    private RadioButton manButton, womanButton;
    private DatePickerDialog datePickerDialog;
    private EditText newBirthdateInput, newNameInput, newForenamesInput,
            newEmailInput, newPhoneInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_patient);

        newPatientText = findViewById(R.id.newPatientText);
        newNameInput = findViewById(R.id.new_name_input);
        newForenamesInput = findViewById(R.id.new_forenames_input);
        newEmailInput = findViewById(R.id.new_email_input);
        newPhoneInput = findViewById(R.id.new_phone_input);
        newBirthdateInput = findViewById(R.id.new_birthdate_input);
        checkIsAdmin = findViewById(R.id.checkIsAdmin);
        manButton = findViewById(R.id.manButton);
        womanButton = findViewById(R.id.womanButton);
        createPatientButton = findViewById(R.id.create_patient_button);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null && bundle.getBoolean("Edit")) {
            newPatientText.setText(R.string.edit_patient_title);
            createPatientButton.setText(R.string.edit_patient_title);
            User patient = bundle.getParcelable("Patient");
            newNameInput.setText(patient.getName());
            newPhoneInput.setText(patient.getPhone());
            password = patient.getPassword();
            newForenamesInput.setText(patient.getForenames());
            newEmailInput.setText(patient.getEmail());
            newEmailInput.setVisibility(View.INVISIBLE);
            measures = patient.getMeasures();
            newBirthdateInput.setText(patient.getBirthdate());

            if (patient.isAdmin()) {
                checkIsAdmin.setChecked(true);
            }
            if (patient.getGender() == 2) {
                manButton.setChecked(true);
            } else if (patient.getGender() == 1) {
                womanButton.setChecked(true);
            }
        }
        createPatientButton.setOnClickListener(v -> {
            if (newNameInput.getText().toString().equals("")
                    || newForenamesInput.getText().toString().equals("")
                    || newEmailInput.getText().toString().equals("")
                    || newPhoneInput.getText().toString().equals("")
                    || newBirthdateInput.getText().toString().equals("")) {
                Snackbar.make(v, getString(R.string.invalid_create_patient), Snackbar.LENGTH_LONG)
                        .setAction("Action", null)
                        .show();
            } else {
                if (Utils.hasEmailFormat(newEmailInput.getText().toString())) {
                    newEmailInput.setTextColor(getResources().getColor(R.color.design_default_color_on_secondary));
                    int gender = 0;
                    boolean admin = false;
                    if (manButton.isChecked()) {
                        gender = 2;
                    } else if (womanButton.isChecked()) {
                        gender = 1;
                    }
                    if (checkIsAdmin.isChecked()) {
                        admin = true;
                    }

                    User user = new User(newBirthdateInput.getText().toString(),
                            newEmailInput.getText().toString(),
                            newForenamesInput.getText().toString(),
                            gender,
                            admin,
                            measures,
                            newNameInput.getText().toString(),
                            password,
                            newPhoneInput.getText().toString());
                    String patientId = Utils.MD5(user.getEmail()).substring(0, 6).toUpperCase();
                    if (user.getPassword() == null) {
                        user.setPassword(patientId);
                    }
                    usersRef.child(patientId).setValue(user);
                    Utils.toast(getApplicationContext(), getString(R.string.patient_created));
                    finish();
                } else {
                    Snackbar.make(v, getString(R.string.invalid_email), Snackbar.LENGTH_LONG)
                            .setAction("Action", null)
                            .show();
                    newEmailInput.setTextColor(getResources().getColor(R.color.design_default_color_error));
                }
            }
        });
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH);
        int year = calendar.get(Calendar.YEAR);

        newBirthdateInput.setOnClickListener(v -> {
            datePickerDialog = new DatePickerDialog(NewPatientActivity.this, (view, year1, month1, dayOfMonth) -> {
                String year2 = String.valueOf(year1);
                String month2 = String.valueOf(month1);
                String day1 = String.valueOf(dayOfMonth);
                if (month1 < 10) {
                    month2 = "0" + month2;
                }
                if (dayOfMonth < 10) {
                    day1 = "0" + day1;
                }
                newBirthdateInput.setText(year2 + "-" + month2 + "-" + day1);
            }, year, month, day);
            datePickerDialog.show();
        });

    }
}
