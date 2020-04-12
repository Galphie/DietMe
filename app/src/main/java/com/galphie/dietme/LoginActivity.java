package com.galphie.dietme;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.telephony.SmsManager;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
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
    public static boolean canFinish = false;
    private static final int PERMISSION_REQUEST_SEND_SMS = 123;
    private static final int PERMISSION_REQUEST_RECEIVE_SMS = 321;
    CheckBox checkRemember, checkShow;
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

        checkRemember = (CheckBox) findViewById(R.id.checkRemember);
        checkShow = (CheckBox) findViewById(R.id.checkShow);

        canFinish = false;

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
                if (checkSMSPermissions()) {
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
//            TODO: En MainActivity, si se recibe el siguiente extra, realizar cambio de contraseña.
            intent.putExtra("Crear contraseña", "crear");
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                    startActivity(intent);
                    finish();
                }
            }, 1000);
        }

        checkShow.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    passInput.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
                } else {
                    passInput.setInputType(129);
                }
            }
        });
        checkSMSPermissions();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        emailInput.setText(preferences.getString("Email", ""));
        passInput.setText(preferences.getString("Password", ""));
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (canFinish) {
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                    finish();
                }
            }, 1000);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkSMSPermissions();
    }

    @SuppressLint("ResourceAsColor")
    public void login(View view) {
        String name = emailInput.getText().toString();
        if (isRegistered(name)) {
            if (Utils.SHA256(passInput.getText().toString()).equals(dbPass) || passInput.getText().toString().equals(dbPass)) {
                Intent intent = new Intent(this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("Welcome", "Bienvenido, " + dbUser);
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
                SharedPreferences.Editor editor = preferences.edit();
                if (checkRemember.isChecked()) {
                    editor.putString("Email", emailInput.getText().toString());
                    editor.putString("Password", passInput.getText().toString());
                    editor.apply();
                } else {
                    editor.putString("Email", "");
                    editor.putString("Password", "");
                    editor.apply();
                }
                startActivity(intent);
                finish();

            } else {
                Snackbar.make(view, getString(R.string.invalid_password), Snackbar.LENGTH_LONG)
                        .setAction("Action", null)
                        .show();
            }

        } else {
            if (emailInput.getText().length() == 0) {
                Snackbar.make(view, "Por favor, introduce algún correo.", Snackbar.LENGTH_LONG)
                        .setAction("Action", null)
                        .show();
            } else {
                Snackbar.make(view, getString(R.string.no_such_email) + emailInput.getText().toString() + ".", Snackbar.LENGTH_LONG)
                        .setAction("Action", null)
                        .show();
            }
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

    protected boolean checkSMSPermissions() {
        boolean granted = false;
        if (Build.VERSION.SDK_INT >= 23) {
            if ((ContextCompat.checkSelfPermission(this,
                    android.Manifest.permission.SEND_SMS)
                    != PackageManager.PERMISSION_GRANTED) || (ContextCompat.checkSelfPermission(this,
                    android.Manifest.permission.RECEIVE_SMS)
                    != PackageManager.PERMISSION_GRANTED)) {
                if (ContextCompat.checkSelfPermission(this,
                        android.Manifest.permission.SEND_SMS)
                        != PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                            android.Manifest.permission.SEND_SMS)) {
                        ActivityCompat.requestPermissions(this,
                                new String[]{android.Manifest.permission.SEND_SMS},
                                PERMISSION_REQUEST_SEND_SMS);
                    } else {
                        ActivityCompat.requestPermissions(this,
                                new String[]{android.Manifest.permission.SEND_SMS},
                                PERMISSION_REQUEST_SEND_SMS);
                    }
                }
                if (ContextCompat.checkSelfPermission(this,
                        android.Manifest.permission.RECEIVE_SMS)
                        != PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                            android.Manifest.permission.RECEIVE_SMS)) {
                        ActivityCompat.requestPermissions(this,
                                new String[]{android.Manifest.permission.RECEIVE_SMS},
                                PERMISSION_REQUEST_RECEIVE_SMS);
                    } else {
                        ActivityCompat.requestPermissions(this,
                                new String[]{android.Manifest.permission.RECEIVE_SMS},
                                PERMISSION_REQUEST_RECEIVE_SMS);
                    }
                }
            } else {
                granted = !granted;
            }
        } else {
            granted = !granted;
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
        checkRemember.setChecked(false);
    }

}
