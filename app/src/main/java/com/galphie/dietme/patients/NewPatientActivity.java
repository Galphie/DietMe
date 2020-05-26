package com.galphie.dietme.patients;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;

import androidx.appcompat.app.AppCompatActivity;

import com.galphie.dietme.R;
import com.galphie.dietme.Utils;
import com.galphie.dietme.instantiable.Measures;
import com.galphie.dietme.instantiable.User;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;

public class NewPatientActivity extends AppCompatActivity {

    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference usersRef = database.getReference("Usuario");

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

        newNameInput = findViewById(R.id.new_name_input);
        newForenamesInput = findViewById(R.id.new_forenames_input);
        newEmailInput = findViewById(R.id.new_email_input);
        newPhoneInput = findViewById(R.id.new_phone_input);
        newBirthdateInput = findViewById(R.id.new_birthdate_input);
        checkIsAdmin = findViewById(R.id.checkIsAdmin);
        manButton = findViewById(R.id.manButton);
        womanButton = findViewById(R.id.womanButton);
        createPatientButton = findViewById(R.id.create_patient_button);
        createPatientButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int gender = 0;
                boolean admin = false;
                String password = Utils.MD5(newEmailInput.toString()).substring(0, 6).toUpperCase();
                if (manButton.isChecked()) {
                    gender = 2;
                } else if (womanButton.isChecked()) {
                    gender = 1;
                }
                if (checkIsAdmin.isChecked()) {
                    admin = true;
                }

                Measures measures = new Measures(0,
                        0,
                        0,
                        0,
                        0,
                        0,
                        0,
                        0,
                        0,
                        0,
                        0,
                        0,
                        0);
                User user = new User(newBirthdateInput.getText().toString(),
                        newEmailInput.getText().toString(),
                        newForenamesInput.getText().toString(),
                        gender,
                        admin,
                        measures,
                        newNameInput.getText().toString(),
                        password,
                        newPhoneInput.getText().toString());
                usersRef.child(Utils.MD5(newEmailInput.toString()).substring(0, 6).toUpperCase()).setValue(user);
                Utils.toast(getApplicationContext(), getString(R.string.patient_created));
                finish();
            }
        });
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH);
        int year = calendar.get(Calendar.YEAR);

        newBirthdateInput.setOnClickListener(v -> {
            datePickerDialog = new DatePickerDialog(NewPatientActivity.this, new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year1, int month1, int dayOfMonth) {
                    String year = String.valueOf(year1);
                    String month = String.valueOf(month1);
                    String day = String.valueOf(dayOfMonth);
                    if (month1 < 10) {
                        month = "0"+month;
                    }
                    if (dayOfMonth < 10) {
                        day = "0"+day;
                    }
                    newBirthdateInput.setText(year + "-" + month + "-" + day);
                }
            }, year, month, day);
            datePickerDialog.show();
        });

    }
}
