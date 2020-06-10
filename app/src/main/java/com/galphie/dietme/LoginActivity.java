package com.galphie.dietme;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.telephony.SmsManager;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.DialogFragment;

import com.galphie.dietme.dialog.AccessRequestDialog;
import com.galphie.dietme.instantiable.User;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

public class LoginActivity extends AppCompatActivity implements AccessRequestDialog.AccessRequestDialogListener, ValueEventListener {

    public static boolean splashed = false;
    public static boolean canFinish = false;

    private FirebaseDatabase database = FirebaseDatabase.getInstance();

    private User currentUser;
    private CheckBox checkRemember;
    private EditText emailInput, passInput;
    private String dbPass = null;
    private ArrayList<User> usersRegistered = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        DatabaseReference usersRef = database.getReference("Usuario");
        usersRef.addValueEventListener(this);

        if (!splashed) {
            Intent intent = new Intent(this, SplashScreen.class);
            startActivity(intent);
            try {
                Thread.sleep(SplashScreen.SPLASH_TIME_OUT);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        setContentView(R.layout.activity_login);

        emailInput = findViewById(R.id.emailInput);
        passInput = findViewById(R.id.passInput);
        Button linkBut = findViewById(R.id.linkBut);
        Button loginBut = findViewById(R.id.loginBut);
        checkRemember = findViewById(R.id.checkRemember);
        CheckBox checkShow = findViewById(R.id.checkShow);

        splashed = false;
        canFinish = false;

        loginBut.setOnClickListener(this::login);
        linkBut.setOnClickListener(v -> {
            if (SmsListener.checkSMSPermissions(this, LoginActivity.this)) {
                showAccessRequestDialog();
            }
        });
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String mail = extras.getString("Email");
            String password = extras.getString("Password");
            emailInput.setText(mail);
            passInput.setText(password);

            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean("isChecked", false);
            editor.apply();
            String jsonCurrentUser = preferences.getString("CurrentUser", "");
            currentUser = new Gson().fromJson(jsonCurrentUser, User.class);
            startLoginActivity(true);
        }

        checkShow.setOnCheckedChangeListener((buttonView, isChecked) -> showPassword(isChecked));
        SmsListener.checkSMSPermissions(this, LoginActivity.this);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        if ((preferences.getBoolean("isChecked", false))) {
            emailInput.setText(preferences.getString("Email", ""));
            passInput.setText(preferences.getString("Password", ""));
            checkRemember.setChecked(true);
        } else {
            checkRemember.setChecked(false);
        }
    }

    private void showPassword(boolean isChecked) {
        if (isChecked) {
            passInput.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
        } else {
            passInput.setInputType(129);
        }
    }

    private void startLoginActivity(boolean accessRequested) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("currentUser", currentUser);
        if (accessRequested) {
            intent.putExtra("accessRequested", true);
            Handler handler = new Handler();
            handler.postDelayed(() -> {
                startActivity(intent);
                finish();
            }, 1000);
        } else {
            startActivity(intent);
        }
    }

    private void showAccessRequestDialog() {
        Bundle args = new Bundle();
        args.putString("mail", emailInput.getText().toString());
        DialogFragment accessRequestDialog = new AccessRequestDialog();
        accessRequestDialog.setArguments(args);
        accessRequestDialog.show(getSupportFragmentManager(), "Acceso solicitado.");
    }

    @SuppressLint("ResourceAsColor")
    public void login(View view) {
        String userEmail = emailInput.getText().toString();
        if (isRegistered(userEmail)) {
            if (Objects.equals(Utils.SHA256(passInput.getText().toString()), dbPass) || passInput.getText().toString().equals(dbPass)) {
                checkIfRemember();
                startLoginActivity(false);
                finish();
            } else {
                Snackbar.make(view, getString(R.string.invalid_password), Snackbar.LENGTH_LONG)
                        .setBackgroundTint(ResourcesCompat.getColor(getResources(),R.color.colorPrimaryDark, null))
                        .setAction("Action", null)
                        .show();
            }
        } else {
            if (emailInput.getText().length() == 0) {
                Snackbar.make(view, getString(R.string.empty_email), Snackbar.LENGTH_LONG)
                        .setBackgroundTint(ResourcesCompat.getColor(getResources(),R.color.colorPrimaryDark, null))
                        .setAction("Action", null)
                        .show();
            } else {
                Snackbar.make(view, getString(R.string.no_such_email) + emailInput.getText().toString() + ".", Snackbar.LENGTH_LONG)
                        .setBackgroundTint(ResourcesCompat.getColor(getResources(),R.color.colorPrimaryDark, null))
                        .setAction("Action", null)
                        .show();
            }
        }
    }

    private void checkIfRemember() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = preferences.edit();
        if (checkRemember.isChecked()) {
            editor.putString("Email", emailInput.getText().toString());
            editor.putString("Password", passInput.getText().toString());
            editor.putBoolean("isChecked", true);
        } else {
            editor.putString("Email", "");
            editor.putString("Password", "");
            editor.putBoolean("isChecked", false);
        }
        editor.apply();
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
                String jsonCurrentUser = gson.toJson(usersRegistered.get(i));
                editor.putString("CurrentUser", jsonCurrentUser);
                editor.apply();
                isRegistered = true;
            }
        }
        if (!isRegistered) {
            Utils.toast(getApplicationContext(), getString(R.string.user_not_found));
        }

    }

    public void sendSms(User user, String phone) {
        String message = user.getName() + ", tus credenciales:\n" +
                "#" + user.getEmail() + "#\n" +
                "#" + user.getPassword() + "#";
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(phone, null, message, null, null);
        checkRemember.setChecked(false);
    }

    @Override
    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
        usersRegistered.clear();
        for (DataSnapshot ds : dataSnapshot.getChildren()) {
            User user = ds.getValue(User.class);
            usersRegistered.add(user);
        }
        Collections.sort(usersRegistered, (o1, o2) -> o1.getEmail().compareToIgnoreCase(o2.getEmail()));
    }

    @Override
    public void onCancelled(@NonNull DatabaseError databaseError) {
        Utils.toast(getApplicationContext(), getString(R.string.database_snapshot_failure));
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (canFinish) {
            Handler handler = new Handler();
            handler.postDelayed(this::finish, 1000);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        SmsListener.checkSMSPermissions(this, LoginActivity.this);
    }
}
