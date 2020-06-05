package com.galphie.dietme;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.provider.Telephony;
import android.telephony.SmsMessage;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import org.jetbrains.annotations.NotNull;

public class SmsListener extends BroadcastReceiver implements ActivityCompat.OnRequestPermissionsResultCallback {

    private static final int PERMISSION_REQUEST_SEND_SMS = 123;
    private static final int PERMISSION_REQUEST_RECEIVE_SMS = 321;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Telephony.Sms.Intents.SMS_RECEIVED_ACTION.equals(intent.getAction())) {
            for (SmsMessage smsMessage : Telephony.Sms.Intents.getMessagesFromIntent(intent)) {
                String messageBody = smsMessage.getMessageBody();
                String[] parts = messageBody.split("#");
                String username = parts[1];
                String password = parts[3];
                Intent login = new Intent(context, LoginActivity.class);
                login.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                login.putExtra("Email", username);
                login.putExtra("Password", password);
                LoginActivity.canFinish = true;
                context.startActivity(login);
            }
        }
    }

    protected static boolean checkSMSPermissions(Context context, Activity activity) {
        boolean granted = false;
        if ((ContextCompat.checkSelfPermission(context,
                android.Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED) || (ContextCompat.checkSelfPermission(context,
                android.Manifest.permission.RECEIVE_SMS)
                != PackageManager.PERMISSION_GRANTED)) {
            if (ContextCompat.checkSelfPermission(context,
                    android.Manifest.permission.SEND_SMS)
                    != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(activity,
                        android.Manifest.permission.SEND_SMS)) {
                    ActivityCompat.requestPermissions(activity,
                            new String[]{android.Manifest.permission.SEND_SMS},
                            PERMISSION_REQUEST_SEND_SMS);
                } else {
                    ActivityCompat.requestPermissions(activity,
                            new String[]{android.Manifest.permission.SEND_SMS},
                            PERMISSION_REQUEST_SEND_SMS);
                }
            }
            if (ContextCompat.checkSelfPermission(context,
                    android.Manifest.permission.RECEIVE_SMS)
                    != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(activity,
                        android.Manifest.permission.RECEIVE_SMS)) {
                    ActivityCompat.requestPermissions(activity,
                            new String[]{android.Manifest.permission.RECEIVE_SMS},
                            PERMISSION_REQUEST_RECEIVE_SMS);
                } else {
                    ActivityCompat.requestPermissions(activity,
                            new String[]{android.Manifest.permission.RECEIVE_SMS},
                            PERMISSION_REQUEST_RECEIVE_SMS);
                }
            }
        } else {
            granted = true;
        }
        return granted;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NotNull String[] permissions, @NotNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_SEND_SMS: {
            }
            case PERMISSION_REQUEST_RECEIVE_SMS: {
            }
        }
    }
}
