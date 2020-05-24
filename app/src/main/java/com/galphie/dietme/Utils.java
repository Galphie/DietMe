package com.galphie.dietme;

import android.content.Context;
import android.icu.text.DateFormat;
import android.icu.text.SimpleDateFormat;
import android.view.Gravity;
import android.widget.Toast;

import java.text.ParseException;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {
    /*
    PATTERN_PASSWORD explanation

                 ^                 # start-of-string
                 (?=.*[0-9])       # a digit must occur at least once
                 (?=.*[a-z])       # a lower case letter must occur at least once
                 (?=.*[A-Z])       # an upper case letter must occur at least once
                 (?=.*[@#$%^&+=|"()?¿¡!'*._,;:])  # a special character must occur at least once
                 .{8,}             # anything, at least eight places though
                 $                 # end-of-string

     */
    private final static String PATTERN_DATE = "yyyy-MM-dd";
    private static final String PATTERN_EMAIL = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
    private static final String PATTERN_PASSWORD = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=|\"()?¿¡!'*._,;:]).{8,}$";

    public static void toast(Context context, String mensaje) {
        Toast toast = Toast.makeText(context, mensaje, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER | Gravity.BOTTOM, 0, 0);
        toast.show();
    }

    public static boolean hasDigits(String password) {
        Pattern pattern = Pattern.compile("^(?=.*[0-9])$");
        Matcher matcher = pattern.matcher(password);
        return matcher.matches();
    }

    public static boolean hasSpecial(String password) {
        Pattern pattern = Pattern.compile("^(?=.*[@#$%^&+=|\"()?¿¡!'*._,;:])$");
        Matcher matcher = pattern.matcher(password);
        return matcher.matches();
    }

    public static boolean hasCompletePasswordFormat(String password) {
        Pattern pattern = Pattern.compile(PATTERN_PASSWORD);
        Matcher matcher = pattern.matcher(password);
        return matcher.matches();
    }

    public static boolean hasEmailFormat(String email) {
        Pattern pattern = Pattern.compile(PATTERN_EMAIL);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    public static String MD5(String md5) {
        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
            byte[] array = md.digest(md5.getBytes());
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < array.length; ++i) {
                sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100).substring(1, 3));
            }
            return sb.toString();
        } catch (java.security.NoSuchAlgorithmException e) {
        }
        return null;
    }

    public static Date stringToDate(String date) {
        DateFormat format = new SimpleDateFormat(PATTERN_DATE);
        Date newDate = null;
        try {
            newDate = format.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return newDate;
    }

    public static String dateToString(Date date) {
        DateFormat df = new SimpleDateFormat(PATTERN_DATE);
        String stringDate = df.format(date);
        return stringDate;
    }

    public static String SHA256(String text) {
        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("SHA-256");
            byte[] array = md.digest(text.getBytes());
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < array.length; i++) {
                sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100).substring(1, 3));
            }
            return sb.toString();
        } catch (java.security.NoSuchAlgorithmException e) {
        }
        return null;
    }

}
