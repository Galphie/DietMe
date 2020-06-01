package com.galphie.dietme;

import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);

        Handler handler = new Handler();
        Runnable runnable = () -> {
            LoginActivity.splashed = true;
            finish();
        };
        handler.postDelayed(runnable, 2500);
    }

    @Override
    public void onBackPressed() {
    }
}
