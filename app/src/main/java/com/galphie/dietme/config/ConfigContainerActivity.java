package com.galphie.dietme.config;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.galphie.dietme.HomeFragment;
import com.galphie.dietme.R;
import com.galphie.dietme.Utils;

public class ConfigContainerActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config_container);

        Bundle bd = getIntent().getExtras();
        if (bd != null){
            int type = bd.getInt("Type");
            switch (type){
                case 1:
                    getSupportFragmentManager()
                            .beginTransaction()
                            .add(R.id.container, new PasswordFragment())
                            .commit();
                    break;
                case 2:
                    getSupportFragmentManager()
                            .beginTransaction()
                            .add(R.id.container,new NotificationFragment())
                            .commit();
                default:
                    break;
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }
}
