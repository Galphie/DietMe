package com.galphie.dietme;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.galphie.dietme.config.ConfigListActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;


public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {
    private User currentUser;
    private BottomNavigationView bottomNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Inicio");
        bottomNav = (BottomNavigationView) findViewById(R.id.bottom_nav);
        Bundle bd = getIntent().getExtras();
        currentUser = getIntent().getParcelableExtra("User");
        Utils.toast(getApplicationContext(), "Bienvenido, " + currentUser.getUsername() + ".");
        if (bd != null) {
            if (bd.getBoolean("ForzarCambio")) {
                Utils.toast(getApplicationContext(), "Redirigiendo a cambio de contraseña");
            }
        }
        init();
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
        switch (item.getItemId()) {
            case R.id.config:
                Intent intent = new Intent(this, ConfigListActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.navigation_home:
                Navigation
                        .findNavController(this, R.id.nav_host_fragment)
                        .navigate(R.id.homeFragment);
                setTitle(getString(R.string.home_title));
                return true;
            case R.id.navigation_appointment:
                Navigation
                        .findNavController(this, R.id.nav_host_fragment)
                        .navigate(R.id.appointmentFragment);
                setTitle(getString(R.string.appointment_title));
                return true;
            case R.id.navigation_patients:
                Navigation
                        .findNavController(this, R.id.nav_host_fragment)
                        .navigate(R.id.patientsFragment);
                setTitle(getString(R.string.patients_title));
                return true;
        }
        return false;
    }
};


