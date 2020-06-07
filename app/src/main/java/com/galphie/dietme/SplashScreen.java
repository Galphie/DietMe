package com.galphie.dietme;

import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

public class SplashScreen extends AppCompatActivity {

    public static final int SPLASH_TIME_OUT = 780;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);

        new Handler().postDelayed(() -> {
            LoginActivity.splashed = true;
            finish();
        }, 1500);
    }

    @Override
    public void onBackPressed() {
    }
}
