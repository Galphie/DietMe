package com.galphie.dietme;

import android.content.Intent;
import android.os.Bundle;
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


public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {
    private User currentUser;
    private BottomNavigationView bottomNav;
    private Toolbar toolbar;

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
        currentUser = getIntent().getParcelableExtra("currentUser");
        init();
        goHome();
        if (bd != null) {
            if (bd.getBoolean("accessRequested")) {
                Intent intent = new Intent(this, ConfigContainerActivity.class);
                intent.putExtra("type", 1);
                intent.putExtra("currentUser", currentUser);
                intent.putExtra("accessRequested", true);
                startActivity(intent);
            }
        }
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
            intent.putExtra("currentUser", currentUser);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.navigation_home:
                Bundle homeBd = new Bundle();
                homeBd.putParcelable("currentUser", currentUser);
                Navigation
                        .findNavController(this, R.id.nav_host_fragment)
                        .navigate(R.id.homeFragment, homeBd);
                if (getSupportActionBar() != null) {
                    getSupportActionBar().setTitle(getString(R.string.home_title));
                    getSupportActionBar().setLogo(R.drawable.ic_home_24dp);
                }
                return true;
            case R.id.navigation_appointment:
                Bundle apptBd = new Bundle();
                apptBd.putParcelable("currentUser", currentUser);
                Navigation
                        .findNavController(this, R.id.nav_host_fragment)
                        .navigate(R.id.appointmentFragment, apptBd);
                if (getSupportActionBar() != null) {
                    getSupportActionBar().setTitle(getString(R.string.appointment_title));
                    getSupportActionBar().setLogo(R.drawable.ic_calendar_24dp);
                }
                return true;
            case R.id.navigation_patients:
                Bundle patBd = new Bundle();
                patBd.putParcelable("currentUser", currentUser);
                Navigation
                        .findNavController(this, R.id.nav_host_fragment)
                        .navigate(R.id.patientsFragment, patBd);
                if (getSupportActionBar() != null) {
                    getSupportActionBar().setTitle(getString(R.string.patients_title));
                    getSupportActionBar().setLogo(R.drawable.ic_person_24dp);
                }
                return true;
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        getSupportActionBar().setTitle(getString(R.string.home_title));
        getSupportActionBar().setLogo(R.drawable.ic_home_24dp);
        bottomNav.setSelectedItemId(R.id.navigation_home);
    }

    public void goHome() {
        Bundle homeBd = new Bundle();
        homeBd.putParcelable("currentUser", currentUser);
        Navigation
                .findNavController(this, R.id.nav_host_fragment)
                .navigate(R.id.homeFragment, homeBd);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getString(R.string.home_title));
            getSupportActionBar().setLogo(R.drawable.ic_home_24dp);
        }
    }
}


