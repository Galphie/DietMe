package com.galphie.dietme;

import android.os.Parcel;
import android.os.Parcelable;

public class User implements Parcelable {
    private String email, forenames, name, password, phone;

    public User() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getForenames() {
        return forenames;
    }

    public void setForenames(String forenames) {
        this.forenames = forenames;
    }

    public static Creator<User> getCREATOR() {
        return CREATOR;
    }

    public User(String email, String forenames, String name, String password, String phone) {
        this.email = email;
        this.password = password;
        this.phone = phone;
        this.name = name;
        this.forenames = forenames;
    }

    protected User(Parcel in) {
        email = in.readString();
        forenames = in.readString();
        name = in.readString();
        password = in.readString();
        phone = in.readString();
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }


    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(email);
        dest.writeString(forenames);
        dest.writeString(name);
        dest.writeString(password);
        dest.writeString(phone);

    }
}
