package com.galphie.dietme;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        String welcome;
        Bundle bd = getIntent().getExtras();
        if (bd != null) {
            welcome = (String) bd.get("Welcome");
            Utils.toast(getApplicationContext(), "Bienvenido, " + welcome);
        }
    }
}
