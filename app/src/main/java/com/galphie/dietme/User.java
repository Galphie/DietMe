package com.galphie.dietme;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

public class User implements Parcelable {
    private String email, forenames, name, password, phone, birthdate;
    private int gender;
//    private Measures measures;


    public User() {
    }

    public User(String birthdate, String email, String forenames, int gender, String name, String password, String phone,
                Measures measures) {
        this.email = email;
        this.forenames = forenames;
        this.name = name;
        this.password = password;
        this.phone = phone;
        this.birthdate = birthdate;
        this.gender = gender;
//        this.measures = measures;
    }

    protected User(Parcel in) {
        birthdate = in.readString();
        email = in.readString();
        forenames = in.readString();
        gender = in.readInt();
        name = in.readString();
        password = in.readString();
        phone = in.readString();
//        measures = in.readParcelable(Measures.class.getClassLoader());
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

    public Date getDateBirthdate() {
        return Utils.stringToDate(birthdate);
    }

    public void setBirthdate(String birthDate) {
        this.birthdate = birthDate;
    }

    public void setDateBirthdate (Date birthdate) {
        this.birthdate = Utils.dateToString(birthdate);
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

//    public Measures getMeasures() {
//        return measures;
//    }
//
//    public void setMeasures(Measures measures) {
//        this.measures = measures;
//    }

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
        dest.writeString(name);
        dest.writeString(password);
        dest.writeString(phone);
//        dest.writeParcelable(measures, flags);
    }
}
