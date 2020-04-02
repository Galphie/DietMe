package com.galphie.dietme;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.provider.Telephony;
import android.telephony.SmsMessage;

public class SmsListener extends BroadcastReceiver {
    private String messageBody = "";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Telephony.Sms.Intents.SMS_RECEIVED_ACTION.equals(intent.getAction())) {
            for (SmsMessage smsMessage : Telephony.Sms.Intents.getMessagesFromIntent(intent)) {
                messageBody = smsMessage.getMessageBody();
                String[] parts = messageBody.split("#");
                String username = parts[1];
                String password = parts[3];
                Intent login = new Intent(context, LoginActivity.class);
                login.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                login.putExtra("Email", username);
                login.putExtra("Password", password);
                context.startActivity(login);
            }
        }
    }
}
