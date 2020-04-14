package com.galphie.dietme;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        String welcome;
        Bundle bd = getIntent().getExtras();
//        if (bd != null) {
//            welcome = (String) bd.get("Crear contraseña");
//            if (welcome.equals("crear")){
//                Intent intent = new Intent(this,ConfigActivity.class);
//                startActivity(intent);
//            }
//        }
        currentUser = getIntent().getParcelableExtra("User");

        Utils.toast(getApplicationContext(), "Bienvenido, " + currentUser.getUsername() + ".");

    }
}
