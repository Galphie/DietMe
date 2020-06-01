package com.galphie.dietme.config;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.galphie.dietme.R;
import com.galphie.dietme.instantiable.User;
import com.galphie.dietme.Utils;

import java.util.Objects;

public class ConfigContainerActivity extends AppCompatActivity {

    public static final int PASSWORD_CODE = 1998;
    public static final int NOTIFICATION_CODE = 1987;
    public static final int APPOINTMENTS_CODE = 1991;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config_container);

        User currentUser = getIntent().getParcelableExtra("currentUser");
        Bundle bd = getIntent().getExtras();
        if (bd != null) {
            int type = bd.getInt("type");
            switch (type) {
                case PASSWORD_CODE:
                    boolean accessRequested = false;
                    if (bd.getBoolean("accessRequested")) {
                        accessRequested = true;
                    }
                    PasswordFragment passwordFragment = PasswordFragment.newInstance(accessRequested, currentUser);
                    getSupportFragmentManager()
                            .beginTransaction()
                            .add(R.id.config_container, passwordFragment)
                            .commit();
                    break;
                case NOTIFICATION_CODE:
                    NotificationFragment notificationFragment = new NotificationFragment();
                    getSupportFragmentManager()
                            .beginTransaction()
                            .add(R.id.config_container, notificationFragment)
                            .commit();
                    break;
                case APPOINTMENTS_CODE:
                    AppointmentsManagementFragment appointmentsManagementFragment = AppointmentsManagementFragment.newInstance(currentUser);
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
        if (Objects.requireNonNull(getIntent().getExtras()).getBoolean("accessRequested")) {
            Utils.toast(getApplicationContext(), getString(R.string.do_not_exit));
        } else {
            super.onBackPressed();
        }
    }

}
