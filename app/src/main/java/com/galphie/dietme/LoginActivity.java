package com.galphie.dietme;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class LoginActivity extends AppCompatActivity implements ConfirmDialogListener {
    private static final int PERMISSION_REQUEST_SEND_SMS = 123;
    private static final int PERMISSION_REQUEST_RECEIVE_SMS = 321;
    EditText emailInput, passInput;
    Button linkBut, loginBut;
    DialogFragment confirmDialog;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference usersRef = database.getReference("Usuario");
    String dbPass = null;
    String dbUser = null;
    private ArrayList<User> usersRegistered = new ArrayList();


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
                    usersRegistered.add(user);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };
        usersRef.addValueEventListener(postListener);
        linkBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkSMSSendPermission() && checkSMSReceivePermission()) {
                    confirmDialog = new ConfirmDialog();
                    confirmDialog.show(getSupportFragmentManager(), "Solicitud código");
                }
            }
        });
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String mail = extras.getString("Email");
            String password = extras.getString("Password");
            emailInput.setText(mail);
            passInput.setText(password);
            final Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            TODO: En MainActivity, si se recibe el siguiente extra, realizar cambio de contraseña
            intent.putExtra("Crear contraseña", "crear");
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                    startActivity(intent);
                    finish();
                }
            }, 1000);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                finish();
            }
        }, 1000);
    }

    @SuppressLint("ResourceAsColor")
    public void login(View view) {
        String name = emailInput.getText().toString();
        if (isRegistered(name)) {
            if (Utils.MD5(passInput.getText().toString()).equals(dbPass) || passInput.getText().toString().equals(dbPass)) {
                Intent intent = new Intent(this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("Welcome", "Bienvenido, " + dbUser);
                startActivity(intent);
                finish();

            } else {
                Snackbar.make(view, R.string.invalid_password, Snackbar.LENGTH_LONG)
                        .setAction("Action", null)
                        .show();
            }

        } else {
            Snackbar.make(view, R.string.no_such_email + emailInput.getText().toString() + ".", Snackbar.LENGTH_LONG)
                    .setAction("Action", null)
                    .show();
        }

    }

    public boolean isRegistered(String user) {
        boolean isCorrect = false;
        for (int i = 0; i < usersRegistered.size(); i++) {
            if ((user.equals(usersRegistered.get(i).getUsername())) || user.equals(usersRegistered.get(i).getEmail())) {
                dbPass = usersRegistered.get(i).getPassword();
                dbUser = usersRegistered.get(i).getUsername();
                isCorrect = true;
            }
        }
        return isCorrect;
    }

    @Override
    public void setInfo(String email, String phone) {
        Utils.toast(getApplicationContext(), getString(R.string.request_submitted));
        boolean isRegistered = false;
        for (int i = 0; i < usersRegistered.size(); i++) {
            if ((email.equals(usersRegistered.get(i).getEmail()))
                    && (phone.equals(usersRegistered.get(i).getPhone()) || phone.equals("+34" + usersRegistered.get(i).getPhone()))) {
                sendSms(usersRegistered.get(i), phone);
                isRegistered = true;
            }
        }
        if (!isRegistered) {
            Utils.toast(getApplicationContext(), getString(R.string.user_not_found));
        }

    }

    private boolean checkSMSReceivePermission() {
        boolean granted = false;
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.RECEIVE_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.RECEIVE_SMS)) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.RECEIVE_SMS},
                        PERMISSION_REQUEST_RECEIVE_SMS);
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.RECEIVE_SMS},
                        PERMISSION_REQUEST_RECEIVE_SMS);
            }
        } else {
            granted = true;
        }
        return granted;
    }

    protected boolean checkSMSSendPermission() {
        boolean granted = false;
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.SEND_SMS)) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.SEND_SMS},
                        PERMISSION_REQUEST_SEND_SMS);
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.SEND_SMS},
                        PERMISSION_REQUEST_SEND_SMS);
            }
        } else {
            granted = true;
        }
        return granted;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_SEND_SMS: {
            }
            case PERMISSION_REQUEST_RECEIVE_SMS: {
            }
        }
    }

    public void sendSms(User user, String phone) {
        String message = user.getUsername() + ", tus credenciales:\n" +
                "#" + user.getEmail() + "#\n" +
                "#" + user.getPassword() + "#";
        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(phone, null, message, null, null);
    }

}
