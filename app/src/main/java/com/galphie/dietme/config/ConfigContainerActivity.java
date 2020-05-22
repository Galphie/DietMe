package com.galphie.dietme.config;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.galphie.dietme.R;
import com.galphie.dietme.User;
import com.galphie.dietme.Utils;

public class ConfigContainerActivity extends AppCompatActivity {

    private boolean cambio = false;
    private User currentUser;
    private NotificationFragment notificationFragment = new NotificationFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config_container);

        currentUser = getIntent().getParcelableExtra("User");
        Bundle bd = getIntent().getExtras();
        if (bd != null) {
            int type = bd.getInt("Type");
            switch (type) {
                case 1:
                    if (bd.getBoolean("Cambio")) {
                        cambio = true;
                    }
                    PasswordFragment passwordFragment = PasswordFragment.newInstance(cambio, currentUser);
                    getSupportFragmentManager()
                            .beginTransaction()
                            .add(R.id.container, passwordFragment)
                            .commit();
                    break;
                case 2:
                    getSupportFragmentManager()
                            .beginTransaction()
                            .add(R.id.container, notificationFragment)
                            .commit();
                    break;
                default:
                    break;
            }
        }

    }

    @Override
    public void onBackPressed() {
        if (getIntent().getExtras().getBoolean("Cambio")) {
            Utils.toast(getApplicationContext(), getString(R.string.do_not_exit));
        } else {
            super.onBackPressed();
        }
    }

}
