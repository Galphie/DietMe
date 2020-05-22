package com.galphie.dietme;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
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
        Utils.toast(getApplicationContext(), "Bienvenido, " + currentUser.getName() + ".");
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("UserID", Utils.MD5(currentUser.getEmail()).substring(0, 6).toUpperCase());
        editor.apply();
        init();
        if (bd != null) {
            if (bd.getBoolean("ForzarCambio")) {
                Intent intent = new Intent(this, ConfigContainerActivity.class);
                intent.putExtra("Type", 1);
                intent.putExtra("User", currentUser);
                intent.putExtra("Cambio", true);
                Handler handler = new Handler();
                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        startActivity(intent);
                    }
                };
                handler.postDelayed(runnable, 1000);
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
        switch (item.getItemId()) {
            case R.id.config:
                Intent intent = new Intent(this, ConfigListActivity.class);
                intent.putExtra("User", (Parcelable) currentUser);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        if (Navigation.findNavController(this, R.id.nav_host_fragment).getCurrentDestination().getId()
                == R.id.appointmentFragment) {
            Navigation
                    .findNavController(this, R.id.nav_host_fragment)
                    .navigate(R.id.homeFragment);
            setTitle(getString(R.string.home_title));
        } else if (Navigation.findNavController(this, R.id.nav_host_fragment).getCurrentDestination().getId()
                == R.id.patientsFragment) {
            Navigation
                    .findNavController(this, R.id.nav_host_fragment)
                    .navigate(R.id.homeFragment);
            setTitle(getString(R.string.home_title));
        } else if (Navigation.findNavController(this, R.id.nav_host_fragment).getCurrentDestination().getId()
                == R.id.homeFragment) {
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


