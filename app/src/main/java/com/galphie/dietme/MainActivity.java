package com.galphie.dietme;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.galphie.dietme.config.ConfigContainerActivity;
import com.galphie.dietme.config.ConfigListActivity;
import com.galphie.dietme.instantiable.User;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;


public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {
    private User currentUser;
    private BottomNavigationView bottomNav;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference usersRef = database.getReference("Usuario");
    Toolbar toolbar;
    private ArrayList<User> patientsList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.activity_main_toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Inicio");
            getSupportActionBar().setLogo(R.drawable.ic_home_24dp);
        }
        bottomNav = findViewById(R.id.activity_main_bottom_nav);
        Bundle bd = getIntent().getExtras();
        currentUser = getIntent().getParcelableExtra("User");
        Utils.toast(getApplicationContext(), "Bienvenido, " + currentUser.getName() + ".");
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("UserID", Objects.requireNonNull(Utils.MD5(currentUser.getEmail())).substring(0, 6).toUpperCase());
        editor.apply();
        init();
        if (bd != null) {
            if (bd.getBoolean("ForzarCambio")) {
                Intent intent = new Intent(this, ConfigContainerActivity.class);
                intent.putExtra("Type", 1);
                intent.putExtra("User", currentUser);
                intent.putExtra("Cambio", true);
                Handler handler = new Handler();
                Runnable runnable = () -> startActivity(intent);
                handler.postDelayed(runnable, 1000);
            }
        }
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                patientsList.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    User user = ds.getValue(User.class);
                    patientsList.add(user);
                    Collections.sort(patientsList, (o1, o2) ->
                            o1.getForenames().compareToIgnoreCase(o2.getForenames()));
                }
            }

            @Override
            public void onCancelled(@NotNull DatabaseError databaseError) {
            }
        };
        usersRef.addValueEventListener(postListener);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void init() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupWithNavController(bottomNav, navController);
        bottomNav.setOnNavigationItemSelectedListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.config) {
            Intent intent = new Intent(this, ConfigListActivity.class);
            intent.putExtra("User", currentUser);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (Objects.requireNonNull(Navigation.findNavController(this, R.id.nav_host_fragment).getCurrentDestination()).getId()
                == R.id.appointmentFragment) {
            Navigation
                    .findNavController(this, R.id.nav_host_fragment)
                    .navigate(R.id.homeFragment);
            Objects.requireNonNull(getSupportActionBar()).setTitle(getString(R.string.home_title));
            getSupportActionBar().setLogo(R.drawable.ic_home_24dp);

        } else if (Objects.requireNonNull(Navigation.findNavController(this, R.id.nav_host_fragment).getCurrentDestination()).getId()
                == R.id.patientsFragment) {
            Navigation
                    .findNavController(this, R.id.nav_host_fragment)
                    .navigate(R.id.homeFragment);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle(getString(R.string.home_title));
                getSupportActionBar().setLogo(R.drawable.ic_home_24dp);
            }
        } else if (Objects.requireNonNull(Navigation.findNavController(this,
                R.id.nav_host_fragment).getCurrentDestination()).getId() == R.id.homeFragment) {
            finish();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.navigation_home:
                Navigation
                        .findNavController(this, R.id.nav_host_fragment)
                        .navigate(R.id.homeFragment);
                if (getSupportActionBar() != null) {
                    getSupportActionBar().setTitle(getString(R.string.home_title));
                    getSupportActionBar().setLogo(R.drawable.ic_home_24dp);
                }
                return true;
            case R.id.navigation_appointment:
                Navigation
                        .findNavController(this, R.id.nav_host_fragment)
                        .navigate(R.id.appointmentFragment);
                if (getSupportActionBar() != null) {
                    getSupportActionBar().setTitle(getString(R.string.appointment_title));
                    getSupportActionBar().setLogo(R.drawable.ic_calendar_24dp);
                }
                return true;
            case R.id.navigation_patients:
                Bundle bd = new Bundle();
                bd.putParcelable("CurrentUser", currentUser);
                bd.putParcelableArrayList("PatientsList", patientsList);
                Navigation
                        .findNavController(this, R.id.nav_host_fragment)
                        .navigate(R.id.patientsFragment, bd);
                if (getSupportActionBar() != null) {
                    getSupportActionBar().setTitle(getString(R.string.patients_title));
                    getSupportActionBar().setLogo(R.drawable.ic_person_24dp);
                }

                return true;
        }
        return false;
    }
}


