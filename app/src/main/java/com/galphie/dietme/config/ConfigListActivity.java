package com.galphie.dietme.config;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.galphie.dietme.R;
import com.galphie.dietme.adapters.ConfigOptionsAdapter;
import com.galphie.dietme.instantiable.Option;
import com.galphie.dietme.instantiable.User;

import java.util.ArrayList;
import java.util.Collections;

public class ConfigListActivity extends AppCompatActivity implements ConfigOptionsAdapter.OnOptionClickListener {

    private User currentUser;
    ArrayList<Option> options = new ArrayList<>();
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config_list);

        currentUser = getIntent().getParcelableExtra("currentUser");

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        init();

        ConfigOptionsAdapter adapter = new ConfigOptionsAdapter(options, this);
        recyclerView.setAdapter(adapter);

    }

    private void init() {
        options.add(new Option(getString(R.string.change_password), ConfigContainerActivity.PASSWORD_CODE));
        options.add(new Option(getString(R.string.manage_notifications), ConfigContainerActivity.NOTIFICATION_CODE));
        if (currentUser.isAdmin()) {
            options.add(new Option(getString(R.string.manage_appointments), ConfigContainerActivity.APPOINTMENTS_CODE));
        }
        options.add(new Option(getString(R.string.availability_management),ConfigContainerActivity.AVAILABILITY_CODE));
        Collections.sort(options, (o1, o2) -> o1.getName().compareToIgnoreCase(o2.getName()));
    }

    @Override
    public void onOptionClick(int position) {
        switch (options.get(position).getCode()) {
            case ConfigContainerActivity.PASSWORD_CODE:
                startConfigContainerActivity(ConfigContainerActivity.PASSWORD_CODE);
                break;
            case ConfigContainerActivity.NOTIFICATION_CODE:
                startConfigContainerActivity(ConfigContainerActivity.NOTIFICATION_CODE);
                break;
            case ConfigContainerActivity.APPOINTMENTS_CODE:
                startConfigContainerActivity(ConfigContainerActivity.APPOINTMENTS_CODE);
                break;
            case ConfigContainerActivity.AVAILABILITY_CODE:
                startConfigContainerActivity(ConfigContainerActivity.AVAILABILITY_CODE);
                break;
        }
    }

    private void startConfigContainerActivity(int passwordCode) {
        Intent passwordChange = new Intent(this, ConfigContainerActivity.class);
        passwordChange.putExtra("currentUser", currentUser);
        passwordChange.putExtra("type", passwordCode);
        startActivity(passwordChange);
    }
}
