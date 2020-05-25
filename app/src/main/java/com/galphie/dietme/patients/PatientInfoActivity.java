package com.galphie.dietme.patients;

import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.DialogFragment;

import com.galphie.dietme.R;
import com.galphie.dietme.Utils;
import com.galphie.dietme.dialog.ConfirmActionDialog;
import com.galphie.dietme.instantiable.User;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class PatientInfoActivity extends AppCompatActivity{

    private static final String TAG = "PatientInfoActivity";
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference usersRef = database.getReference("Usuario");
    private CardView emailCardView, phoneCardView, ageGenderCardView, idCardView;
    private String patientId;
    private User patient;
    private User currentUser;

    private GestureDetector mGestureDetector;
    private FrameLayout container;
    private TabLayout tabLayout;
    private Toolbar toolbar;
    private TextView patientEmailText, patientPhoneText, patientAgeText,
            patientGenderText, patientIdText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_info);

        if (getIntent().getExtras() != null) {
            this.patient = getIntent().getExtras().getParcelable("Patient");
            this.patientId = getIntent().getExtras().getString("PatientID");
            this.currentUser = getIntent().getExtras().getParcelable("CurrentUser");
        }

        container = findViewById(R.id.patient_info_container);
        BodyCompositionFragment bodyCompositionFragment = BodyCompositionFragment.newInstance(patient, patientId);
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.patient_info_container, bodyCompositionFragment)
                .commit();

        tabLayout = findViewById(R.id.patientInfoTabLayout);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tabLayout.getSelectedTabPosition()) {
                    case 0:
                        BodyCompositionFragment bodyCompositionFragment = BodyCompositionFragment.newInstance(patient, patientId);
                        getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.patient_info_container, bodyCompositionFragment)
                                .commit();
                        break;
                    case 1:
                        PatientAppointmentsFragment patientAppointmentsFragment = PatientAppointmentsFragment.newInstance("", "");
                        getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.patient_info_container, patientAppointmentsFragment)
                                .commit();
                        break;
                    case 2:
                        SharedFilesFragment sharedFilesFragment = SharedFilesFragment.newInstance("", "");
                        getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.patient_info_container, sharedFilesFragment)
                                .commit();
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

        ageGenderCardView = findViewById(R.id.age_genderCardView);
        idCardView = findViewById(R.id.idCardView);
        phoneCardView = findViewById(R.id.phoneCardView);
        phoneCardView.setOnClickListener(v -> {
            if (currentUser.getEmail().equals("algparis96@gmail.com") || currentUser.getPhone().equals("648970252")) {
                Bundle args = new Bundle();
                args.putString("Message", "¿Llamar a " + setPhoneFormat(patient.getPhone()) + "?");
                args.putString("Type", "Call");
                args.putString("Object", patient.getPhone());
                DialogFragment confirmActionDialog = new ConfirmActionDialog();
                confirmActionDialog.setArguments(args);
                confirmActionDialog.show(getSupportFragmentManager(), "Confirmar");
            } else {
                Utils.toast(getApplicationContext(), getString(R.string.developer_action_only));
            }
        });
        phoneCardView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (currentUser.getEmail().equals("algparis96@gmail.com") || currentUser.getPhone().equals("648970252")) {
                    Utils.copyToClipboard(getApplicationContext(), patient.getPhone());
                    Utils.toast(getApplicationContext(), getString(R.string.copied_to_clipboard));
                } else {
                    Utils.toast(getApplicationContext(), getString(R.string.developer_action_only));
                }
                return true;
            }
        });

        emailCardView = findViewById(R.id.emailCardView);
        emailCardView.setOnClickListener(v -> {
            if (currentUser.getEmail().equals("algparis96@gmail.com") || currentUser.getPhone().equals("648970252")) {
                Bundle args = new Bundle();
                args.putString("Message", "¿Enviar un correo a " + patient.getEmail() + "?");
                args.putString("Type", "Email");
                args.putString("Object", patient.getEmail());
                DialogFragment confirmActionDialog = new ConfirmActionDialog();
                confirmActionDialog.setArguments(args);
                confirmActionDialog.show(getSupportFragmentManager(), "Confirmar");
            } else {
                Utils.toast(getApplicationContext(), getString(R.string.developer_action_only));
            }
        });
        emailCardView.setOnLongClickListener(v -> {
            if (currentUser.getEmail().equals("algparis96@gmail.com") || currentUser.getPhone().equals("648970252")) {
                Utils.copyToClipboard(getApplicationContext(), patient.getEmail());
                Utils.toast(getApplicationContext(), getString(R.string.copied_to_clipboard));
            } else {
                Utils.toast(getApplicationContext(), getString(R.string.developer_action_only));
            }
            return true;
        });

        patientEmailText = findViewById(R.id.patient_email);
        patientPhoneText = findViewById(R.id.patient_phone);
        patientAgeText = findViewById(R.id.patient_age);
        patientGenderText = findViewById(R.id.patient_gender);
        patientIdText = findViewById(R.id.patient_id);
        if (currentUser.getEmail().equals("algparis96@gmail.com") || currentUser.getPhone().equals("648970252")) {
            patientEmailText.setText(patient.getEmail());
            patientPhoneText.setText(setPhoneFormat(patient.getPhone()));
        } else {
            patientEmailText.setText(R.string.developer_info_only);
            patientPhoneText.setText(R.string.developer_info_only);
        }
        int age = Utils.calculateAge(patient.getBirthdate());
        patientAgeText.setText(String.valueOf(age));
        if (patient.getGender() == 1) {
            patientGenderText.setText(getString(R.string.female));
        } else {
            patientGenderText.setText(getString(R.string.male));
        }
        patientIdText.setText(patientId);
        toolbar = findViewById(R.id.activity_patient_info_toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(patient.getName() + " " + patient.getForenames());
        }
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        DatabaseReference patientRef = database.getReference("Usuario/" + patientId);

    }

    public static String setPhoneFormat(String phone) {
        return "+34 " + phone.substring(0, 3) + " " +
                phone.substring(3, 6) + " " + phone.substring(6, 9);
    }

}
