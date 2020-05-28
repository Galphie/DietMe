package com.galphie.dietme.config;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.galphie.dietme.R;
import com.galphie.dietme.instantiable.User;
import com.galphie.dietme.Utils;

public class ConfigContainerActivity extends AppCompatActivity {

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
                    boolean cambio = false;
                    if (bd.getBoolean("Cambio")) {
                        cambio = true;
                    }
                    PasswordFragment passwordFragment = PasswordFragment.newInstance(cambio, currentUser);
                    getSupportFragmentManager()
                            .beginTransaction()
                            .add(R.id.config_container, passwordFragment)
                            .commit();
                    break;
                case 2:
                    getSupportFragmentManager()
                            .beginTransaction()
                            .add(R.id.config_container, notificationFragment)
                            .commit();
                    break;
                case 3:
                    AppointmentsManagementFragment appointmentsManagementFragment = AppointmentsManagementFragment.newInstance(null, currentUser);
                    getSupportFragmentManager()
                            .beginTransaction()
                            .add(R.id.config_container, appointmentsManagementFragment)
                            .commit();
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
