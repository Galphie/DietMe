package com.galphie.dietme.config;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.galphie.dietme.R;
import com.galphie.dietme.Utils;

import java.util.ArrayList;

public class ConfigListActivity extends AppCompatActivity implements ConfigOptionsAdapter.OnOptionClickListener {

    ArrayList<Option> opciones = new ArrayList<>();
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config_list);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        init();

        ConfigOptionsAdapter adapter = new ConfigOptionsAdapter(opciones, this);
        recyclerView.setAdapter(adapter);

    }

    private void init() {
        opciones.add(new Option(getString(R.string.change_password), "password"));
        opciones.add(new Option(getString(R.string.manage_notifications), "notifications"));
        opciones.add(new Option("Opción 3", ""));
        opciones.add(new Option("Opción 4", ""));
        opciones.add(new Option("Opción 5", ""));
        opciones.add(new Option("Opción 6", ""));

    }

    @Override
    public void onOptionClick(int position) {
        if (opciones.get(position).getCode().equals("password")) {
            Intent intent = new Intent(this, ConfigContainerActivity.class);
            intent.putExtra("Type",1);
            startActivity(intent);
        } else if (opciones.get(position).getCode().equals("notifications")) {
            Intent intent = new Intent(this, ConfigContainerActivity.class);
            intent.putExtra("Type",2);
            startActivity(intent);
        }
    }
}
