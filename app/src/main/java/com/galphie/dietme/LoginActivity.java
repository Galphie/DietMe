package com.galphie.dietme;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class LoginActivity extends AppCompatActivity implements ConfirmDialogListener {
    EditText emailInput, passInput;
    Button linkBut, loginBut;
    DialogFragment confirmDialog;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("Usuario");
    String dbPass = null;
    String dbUser = null;
    ArrayList<User> listaUsuarios = new ArrayList();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailInput = (EditText) findViewById(R.id.emailInput);
        passInput = (EditText) findViewById(R.id.passInput);
        linkBut = (Button) findViewById(R.id.linkBut);
        loginBut = (Button) findViewById(R.id.loginBut);


        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    User user = ds.getValue(User.class);
                    listaUsuarios.add(user);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };
        myRef.addValueEventListener(postListener);

        linkBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmDialog = new ConfirmDialog();
                confirmDialog.show(getSupportFragmentManager(), "Solicitud código");
            }
        });
    }

    @SuppressLint("ResourceAsColor")
    public void login(View view) {
        String nombre = emailInput.getText().toString();
        if (validarLogin(nombre)) {
            if (Utils.MD5(passInput.getText().toString()).equals(dbPass)) {
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                intent.putExtra("Welcome", dbUser);

            } else {
                Snackbar.make(view, "Contraseña no válida.", Snackbar.LENGTH_LONG)
                        .setAction("Action", null)
                        .show();
            }

        } else {
            Snackbar.make(view, "No existe ningún usuario registrado con el correo " + emailInput.getText().toString() + ".", Snackbar.LENGTH_LONG)
                    .setAction("Action", null)
                    .show();
        }

    }

    public boolean validarLogin(String user) {
        boolean existe = false;
        for (int i = 0; i < listaUsuarios.size(); i++) {
            if ((user.equals(listaUsuarios.get(i).getUsername())) || user.equals(listaUsuarios.get(i).getEmail())) {
                dbPass = listaUsuarios.get(i).getPassword();
                dbUser = listaUsuarios.get(i).getUsername();
                existe = true;
            }
        }
        return existe;
    }


    @Override
    public void onConfirm() {
        Utils.toast(getApplicationContext(), "Solicitud enviada. En breve recibirá respuesta.");
    }

    @Override
    public void setInfo(String email, String phone) {
        //TODO enviar código de acceso
    }
}
