package com.galphie.dietme;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.ActionCodeSettings;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {
    EditText emailInput, passInput;
    Button linkBut, loginBut;

    ActionCodeSettings actionCodeSettings =
            ActionCodeSettings.newBuilder()
                    .setUrl("dietme-39c69.firebaseapp.com")
                    .setHandleCodeInApp(true)
                    .setIOSBundleId("com.example.ios")
                    .setAndroidPackageName(
                            "com.galphie.dietme",
                            true, /* installIfNotAvailable */
                            "12"    /* minimumVersion */)
                    .build();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailInput = (EditText) findViewById(R.id.emailInput);
        passInput = (EditText) findViewById(R.id.passInput);
        linkBut = (Button) findViewById(R.id.linkBut);
        loginBut = (Button) findViewById(R.id.loginBut);


    }

    public void askForLink(View view) {
        String email = emailInput.getText().toString();
        if (!Utils.validarEmail(email)) {
            Utils.toast(getApplicationContext(), "Por favor, introduce un email válido.");
        } else {
            sendSignInLink(email, actionCodeSettings);
        }
    }

    public void sendSignInLink(String email, ActionCodeSettings actionCodeSettings) {
        // [START auth_send_sign_in_link]
        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.sendSignInLinkToEmail(email, actionCodeSettings)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.i("Resultado", "E-mail enviado.");
                            Utils.toast(getApplicationContext(),"¡Hecho! Revisa tu correo electrónico.");
                        }
                    }
                });
        // [END auth_send_sign_in_link]
    }


}
