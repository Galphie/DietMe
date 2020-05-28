package com.galphie.dietme;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.telephony.SmsManager;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import com.galphie.dietme.dialog.AccessRequestDialog;
import com.galphie.dietme.dialog.AccessRequestDialogListener;
import com.galphie.dietme.instantiable.User;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.util.ArrayList;

public class LoginActivity extends AppCompatActivity implements AccessRequestDialogListener {
    public static boolean canFinish = false;
    private static final int PERMISSION_REQUEST_SEND_SMS = 123;
    private static final int PERMISSION_REQUEST_RECEIVE_SMS = 321;
    public User currentUser;
    CheckBox checkRemember, checkShow;
    EditText emailInput, passInput;
    Button linkBut, loginBut;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference usersRef = database.getReference("Usuario");
    String dbPass = null;
    private ArrayList<User> usersRegistered = new ArrayList();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailInput = findViewById(R.id.emailInput);
        passInput = findViewById(R.id.passInput);

        linkBut = findViewById(R.id.linkBut);
        loginBut = findViewById(R.id.loginBut);

        checkRemember = findViewById(R.id.checkRemember);
        checkShow = findViewById(R.id.checkShow);

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
        linkBut.setOnClickListener(v -> {
            if (checkSMSPermissions()) {
                Bundle args = new Bundle();
                args.putString("mail", emailInput.getText().toString());
                DialogFragment accessRequestDialog = new AccessRequestDialog();
                accessRequestDialog.setArguments(args);
                accessRequestDialog.show(getSupportFragmentManager(), "Solicitud código");
            }
        });
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
            SharedPreferences.Editor editor = preferences.edit();
            String mail = extras.getString("Email");
            String password = extras.getString("Password");
            emailInput.setText(mail);
            passInput.setText(password);
            Gson gson = new Gson();
            String json = preferences.getString("CurrentUser", "");
            currentUser = gson.fromJson(json, User.class);
            final Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("User", (Parcelable) currentUser);
            intent.putExtra("ForzarCambio", true);
            editor.putString("Checked", "false");
            editor.apply();
            Handler handler = new Handler();
            handler.postDelayed(() -> {
                startActivity(intent);
                finish();
            }, 1000);
        }

        checkShow.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                passInput.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
            } else {
                passInput.setInputType(129);
            }
        });
        checkSMSPermissions();

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        if ((preferences.getString("Checked", "")).equals("true")) {
            emailInput.setText(preferences.getString("Email", ""));
            passInput.setText(preferences.getString("Password", ""));
            checkRemember.setChecked(true);
        } else {
            checkRemember.setChecked(false);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (canFinish) {
            Handler handler = new Handler();
            handler.postDelayed(() -> finish(), 1000);
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
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("User", currentUser);
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("Email", emailInput.getText().toString());
                editor.putString("Password", passInput.getText().toString());
                if (checkRemember.isChecked()) {
                    editor.putString("Checked", "true");
                    editor.apply();
                } else {
                    editor.putString("Checked", "false");
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
                Snackbar.make(view, getString(R.string.empty_email), Snackbar.LENGTH_LONG)
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
            if (user.equals(usersRegistered.get(i).getEmail())) {
                dbPass = usersRegistered.get(i).getPassword();
                currentUser = usersRegistered.get(i);
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
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
                SharedPreferences.Editor editor = preferences.edit();
                Gson gson = new Gson();
                String json = gson.toJson(usersRegistered.get(i));
                editor.putString("CurrentUser", json);
                editor.apply();
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
        String message = user.getName() + ", tus credenciales:\n" +
                "#" + user.getEmail() + "#\n" +
                "#" + user.getPassword() + "#";
        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(phone, null, message, null, null);
        checkRemember.setChecked(false);
    }

}
