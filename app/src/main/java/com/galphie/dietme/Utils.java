package com.galphie.dietme;

import android.content.Context;
import android.view.Gravity;
import android.widget.Toast;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {
    private static final String PATTERN_EMAIL = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
            + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

    public static void toast(Context context, String mensaje) {
        Toast toast = Toast.makeText(context, mensaje, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER | Gravity.BOTTOM, 0, 0);
        toast.show();
    }

    public static boolean validarEmail(String email) {
        Pattern pattern = Pattern.compile(PATTERN_EMAIL);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }
}
