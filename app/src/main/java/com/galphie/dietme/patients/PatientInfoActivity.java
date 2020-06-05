package com.galphie.dietme.patients;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import com.galphie.dietme.dialog.UploadFileDialog;
import com.galphie.dietme.instantiable.User;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Objects;

public class PatientInfoActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener,
        TabLayout.OnTabSelectedListener, ValueEventListener {

    private static final int PDF_CODE = 1000;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private String patientId;
    private User patient;
    private User currentUser;

    private ViewPager pager;
    private TabLayout tabLayout;
    private TextView patientEmailText;
    private TextView patientPhoneText;
    private TextView patientAgeText;
    private TextView patientGenderText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_info);

        if (getIntent().getExtras() != null) {
            this.patient = getIntent().getExtras().getParcelable("patient");
            this.currentUser = getIntent().getExtras().getParcelable("currentUser");

            this.patientId = Objects.requireNonNull(Utils.MD5(patient.getEmail())).substring(0, 6).toUpperCase();
        }

        Toolbar toolbar = findViewById(R.id.activity_patient_info_toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(patient.getName() + " " + patient.getForenames());
        }
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
        toolbar.setOverflowIcon(getDrawable(R.drawable.ic_more_vert_white_24dp));

        ArrayList<Fragment> fragments = setTabs();

        tabLayout = findViewById(R.id.patientInfoTabLayout);
        pager = findViewById(R.id.patient_info_pager);
        PagerAdapter pagerAdapter = new SlidePagerAdapter(getSupportFragmentManager(), fragments);

        pager.setAdapter(pagerAdapter);
        pager.addOnPageChangeListener(this);
        tabLayout.addOnTabSelectedListener(this);

        CardView phoneCardView = findViewById(R.id.phoneCardView);
        CardView emailCardView = findViewById(R.id.emailCardView);
        patientEmailText = findViewById(R.id.patient_email);
        patientPhoneText = findViewById(R.id.patient_phone);
        patientAgeText = findViewById(R.id.patient_age);
        patientGenderText = findViewById(R.id.patient_gender);
        TextView patientIdText = findViewById(R.id.patient_id);

        patientIdText.setText(patientId);

        DatabaseReference patientRef = database.getReference("Usuario").child(patientId);
        patientRef.addValueEventListener(this);

        phoneCardView.setOnClickListener(v -> {
            if (currentUser.isAdmin()) {
                Bundle args = new Bundle();
                args.putString("confirm_action_dialog_message", "¿Llamar a " + setPhoneFormat(patient.getPhone()) + "?");
                args.putInt("type", ConfirmActionDialog.CALL_CODE);
                args.putString("object", patient.getPhone());
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
                args.putString("confirm_action_dialog_message", "¿Enviar un correo a " + patient.getEmail() + "?");
                args.putInt("type", ConfirmActionDialog.EMAIL_CODE);
                args.putString("object", patient.getEmail());
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
    }

    @NotNull
    private ArrayList<Fragment> setTabs() {
        ArrayList<Fragment> fragments = new ArrayList<>();
        fragments.add(BodyCompositionFragment.newInstance(patient));
        fragments.add(PatientAppointmentsFragment.newInstance(patientId, patient));
        fragments.add(SharedFilesFragment.newInstance(currentUser, patientId));
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
                    intent.putExtra("edit", true);
                    intent.putExtra("patient", patient);
                    startActivity(intent);
                } else {
                    Utils.toast(getApplicationContext(), getString(R.string.developer_action_only));
                }
                return true;
            case R.id.patient_info_upload:
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("application/pdf");
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                startActivityForResult(Intent.createChooser(intent, "Selecciona un pdf"), PDF_CODE);
                return true;
            case R.id.patient_info_remove:
                if (currentUser.isAdmin()) {
                    Bundle args = new Bundle();
                    args.putString("confirm_action_dialog_message", "¿Eliminar a " + patient.getName() + "?");
                    args.putInt("type", ConfirmActionDialog.DELETE_PATIENT_CODE);
                    args.putString("object", patientId);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PDF_CODE && resultCode == RESULT_OK && data != null) {

            UploadFileDialog dialog = new UploadFileDialog();
            Bundle bundle = new Bundle();
            bundle.putString("patientId", patientId);
            bundle.putString("pdf", Objects.requireNonNull(data.getData()).toString());
            dialog.setArguments(bundle);
            dialog.show(getSupportFragmentManager(), "Upload");
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

//    OnPageChangeListener methods

    @Override
    public void onPageSelected(int position) {
        tabLayout.selectTab(tabLayout.getTabAt(position));
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

//    OnTabSelectedListener methods

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

//    ValueEventListener methods

    @Override
    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
        if (dataSnapshot.getValue() == null) {
            finish();
        } else {
            patient.setName(Objects.requireNonNull(dataSnapshot.getValue(User.class)).getName());
            patient.setForenames(Objects.requireNonNull(dataSnapshot.getValue(User.class)).getForenames());
            patient.setEmail(Objects.requireNonNull(dataSnapshot.getValue(User.class)).getEmail());
            patient.setBirthdate(Objects.requireNonNull(dataSnapshot.getValue(User.class)).getBirthdate());
            patient.setGender(Objects.requireNonNull(dataSnapshot.getValue(User.class)).getGender());
            patient.setPhone(Objects.requireNonNull(dataSnapshot.getValue(User.class)).getPhone());
            patient.setMeasures(Objects.requireNonNull(dataSnapshot.getValue(User.class)).getMeasures());

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
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle(patient.getName() + " " + patient.getForenames());
            }
        }
    }

    @Override
    public void onCancelled(@NonNull DatabaseError databaseError) {

    }

    @Override
    protected void onPause() {
        super.onPause();
    }
}
