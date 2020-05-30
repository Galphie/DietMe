package com.galphie.dietme.patients;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.galphie.dietme.R;
import com.galphie.dietme.Utils;
import com.galphie.dietme.adapters.SlidePagerAdapter;
import com.galphie.dietme.dialog.ConfirmActionDialog;
import com.galphie.dietme.instantiable.User;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class PatientInfoActivity extends AppCompatActivity {

    private static final String TAG = "PatientInfoActivity";
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference usersRef = database.getReference("Usuario");
    private CardView emailCardView, phoneCardView, ageGenderCardView, idCardView;
    private String patientId;
    private User patient;
    private User currentUser;

    private ViewPager pager;
    private PagerAdapter pagerAdapter;
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

        ArrayList<Fragment> fragments = setTabs();

        pager = findViewById(R.id.patient_info_pager);
        pagerAdapter = new SlidePagerAdapter(getSupportFragmentManager(), fragments);
        pager.setAdapter(pagerAdapter);
        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                tabLayout.selectTab(tabLayout.getTabAt(position));
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        tabLayout = findViewById(R.id.patientInfoTabLayout);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                pager.setCurrentItem(tabLayout.getSelectedTabPosition());
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
        emailCardView = findViewById(R.id.emailCardView);
        patientEmailText = findViewById(R.id.patient_email);
        patientPhoneText = findViewById(R.id.patient_phone);
        patientAgeText = findViewById(R.id.patient_age);
        patientGenderText = findViewById(R.id.patient_gender);
        patientIdText = findViewById(R.id.patient_id);

        DatabaseReference patientRef = database.getReference("Usuario").child(patientId);
        patientRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                patient.setName(dataSnapshot.getValue(User.class).getName());
                patient.setForenames(dataSnapshot.getValue(User.class).getForenames());
                patient.setEmail(dataSnapshot.getValue(User.class).getEmail());
                patient.setBirthdate(dataSnapshot.getValue(User.class).getBirthdate());
                patient.setGender(dataSnapshot.getValue(User.class).getGender());
                patient.setPhone(dataSnapshot.getValue(User.class).getPhone());
                patient.setMeasures(dataSnapshot.getValue(User.class).getMeasures());

                int age = Utils.calculateAge(patient.getBirthdate());
                patientAgeText.setText(String.valueOf(age));
                if (currentUser.isAdmin()) {
                    patientEmailText.setText(patient.getEmail());
                    patientPhoneText.setText(setPhoneFormat(patient.getPhone()));
                } else {
                    patientEmailText.setText(R.string.developer_info_only);
                    patientPhoneText.setText(R.string.developer_info_only);
                }
                if (patient.getGender() == 1) {
                    patientGenderText.setText(getString(R.string.female));
                } else if (patient.getGender() == 2) {
                    patientGenderText.setText(getString(R.string.male));
                } else {
                    patientGenderText.setText(R.string.not_specified);
                }
            }

            @Override
            public void onCancelled(@NotNull DatabaseError databaseError) {
            }
        });

        phoneCardView.setOnClickListener(v -> {
            if (currentUser.isAdmin()) {
                Bundle args = new Bundle();
                args.putString("Message", "¿Llamar a " + setPhoneFormat(patient.getPhone()) + "?");
                args.putString("Type", "Call");
                args.putString("Object", patient.getPhone());
                DialogFragment confirmActionDialog = new ConfirmActionDialog();
                confirmActionDialog.setArguments(args);
                confirmActionDialog.show(getSupportFragmentManager(), "Confirm");
            } else {
                Utils.toast(getApplicationContext(), getString(R.string.developer_action_only));
            }
        });
        phoneCardView.setOnLongClickListener(v -> {
            if (currentUser.isAdmin()) {
                Utils.copyToClipboard(getApplicationContext(), patient.getPhone());
                Utils.toast(getApplicationContext(), getString(R.string.copied_to_clipboard));
            } else {
                Utils.toast(getApplicationContext(), getString(R.string.developer_action_only));
            }
            return true;
        });
        emailCardView.setOnClickListener(v -> {
            if (currentUser.isAdmin()) {
                Bundle args = new Bundle();
                args.putString("Message", "¿Enviar un correo a " + patient.getEmail() + "?");
                args.putString("Type", "Email");
                args.putString("Object", patient.getEmail());
                DialogFragment confirmActionDialog = new ConfirmActionDialog();
                confirmActionDialog.setArguments(args);
                confirmActionDialog.show(getSupportFragmentManager(), "Confirm");
            } else {
                Utils.toast(getApplicationContext(), getString(R.string.developer_action_only));
            }
        });
        emailCardView.setOnLongClickListener(v -> {
            if (currentUser.isAdmin()) {
                Utils.copyToClipboard(getApplicationContext(), patient.getEmail());
                Utils.toast(getApplicationContext(), getString(R.string.copied_to_clipboard));
            } else {
                Utils.toast(getApplicationContext(), getString(R.string.developer_action_only));
            }
            return true;
        });

        patientIdText.setText(patientId);
        toolbar = findViewById(R.id.activity_patient_info_toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(patient.getName() + " " + patient.getForenames());
        }
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
        toolbar.setOverflowIcon(getDrawable(R.drawable.ic_more_vert_white_24dp));
    }

    @NotNull
    private ArrayList<Fragment> setTabs() {
        ArrayList<Fragment> fragments = new ArrayList<>();
        fragments.add(BodyCompositionFragment.newInstance(patient, patientId));
        fragments.add(new PatientAppointmentsFragment());
        fragments.add(SharedFilesFragment.newInstance(patient, patientId));
        return fragments;
    }

    public static String setPhoneFormat(String phone) {
        return "+34 " + phone.substring(0, 3) + " " +
                phone.substring(3, 6) + " " + phone.substring(6, 9);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.patient_info_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.patient_info_edit:
                if (currentUser.isAdmin()) {
                    Intent intent = new Intent(getApplicationContext(), NewPatientActivity.class);
                    intent.putExtra("Edit", true);
                    intent.putExtra("Patient", patient);
                    startActivity(intent);
                } else {
                    Utils.toast(getApplicationContext(), getString(R.string.developer_action_only));
                }
                return true;
            case R.id.patient_info_upload:
                Utils.toast(getApplicationContext(), "Subir archivo");
                return true;
            case R.id.patient_info_remove:
                if (currentUser.isAdmin()) {
                    Bundle args = new Bundle();
                    args.putString("Message", "¿Eliminar a " + patient.getName() + "?");
                    args.putString("Type", "Delete");
                    args.putString("Object", Utils.MD5(patient.getEmail()).substring(0, 6));
                    DialogFragment confirmActionDialog = new ConfirmActionDialog();
                    confirmActionDialog.setArguments(args);
                    confirmActionDialog.show(getSupportFragmentManager(), "Confirm");
                } else {
                    Utils.toast(getApplicationContext(), getString(R.string.developer_action_only));
                }
                return true;
            default:
                return false;
        }
    }
}
