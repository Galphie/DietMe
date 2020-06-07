package com.galphie.dietme.instantiable;

import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.RequiresApi;

public class User implements Parcelable {
    private String email, forenames, name, password, phone, birthdate;
    private int gender;
    private boolean admin;
    private Measures measures;


    public User() {
    }

    public User(String birthdate, String email, String forenames, int gender, boolean admin,
                Measures measures, String name, String password, String phone) {
        this.birthdate = birthdate;
        this.email = email;
        this.forenames = forenames;
        this.gender = gender;
        this.admin = admin;
        this.measures = measures;
        this.name = name;
        this.password = password;
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getForenames() {
        return forenames;
    }

    public void setForenames(String forenames) {
        this.forenames = forenames;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(String birthdate) {
        this.birthdate = birthdate;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public boolean isAdmin() {
        return admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }

    public Measures getMeasures() {
        return measures;
    }

    public void setMeasures(Measures measures) {
        this.measures = measures;
    }

    protected User(Parcel in) {
        birthdate = in.readString();
        email = in.readString();
        forenames = in.readString();
        gender = in.readInt();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            admin = in.readBoolean();
        }
        measures = in.readParcelable(Measures.class.getClassLoader());
        name = in.readString();
        password = in.readString();
        phone = in.readString();
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @RequiresApi(api = Build.VERSION_CODES.Q)
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(birthdate);
        dest.writeString(email);
        dest.writeString(forenames);
        dest.writeInt(gender);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            dest.writeBoolean(admin);
        }
        dest.writeParcelable(measures, flags);
        dest.writeString(name);
        dest.writeString(password);
        dest.writeString(phone);
    }
}
