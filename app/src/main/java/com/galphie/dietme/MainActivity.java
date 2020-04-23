package com.galphie.dietme;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Bundle bd = getIntent().getExtras();
        currentUser = getIntent().getParcelableExtra("User");

        Utils.toast(getApplicationContext(), "Bienvenido, " + currentUser.getUsername() + ".");
        if (bd != null) {
            if (bd.getBoolean("ForzarCambio")) {
                Utils.toast(getApplicationContext(), "Yendo a cambio de contraseña");
            }
        }

    }
}
