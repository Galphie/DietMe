package com.galphie.dietme;

import android.os.Bundle;
import android.widget.Toolbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.google.android.material.bottomnavigation.BottomNavigationView;


public class MainActivity extends AppCompatActivity {
    private User currentUser;
    private NavController navController;
    private BottomNavigationView bottomNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNav = (BottomNavigationView) findViewById(R.id.bottom_nav);
        navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        Bundle bd = getIntent().getExtras();
        currentUser = getIntent().getParcelableExtra("User");

        Utils.toast(getApplicationContext(), "Bienvenido, " + currentUser.getUsername() + ".");
        if (bd != null) {
            if (bd.getBoolean("ForzarCambio")) {
                Utils.toast(getApplicationContext(), "Redirigiendo a cambio de contraseña");
            }
        }

    }
}
