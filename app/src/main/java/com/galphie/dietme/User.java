package com.galphie.dietme;

import android.os.Parcel;
import android.os.Parcelable;

public class User implements Parcelable {
    private String email, password, username, phone;

    public User() {}

    public User(String email, String password, String phone, String username) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.phone = phone;
    }

    protected User(Parcel in) {
        email = in.readString();
        password = in.readString();
        phone = in.readString();
        username = in.readString();
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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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
        dest.writeString(password);
        dest.writeString(phone);
        dest.writeString(username);

    }
}
