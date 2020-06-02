package com.galphie.dietme.instantiable;

import android.os.Parcel;
import android.os.Parcelable;

public class Appointment implements Parcelable {

    private User patient;
    private String time;
    private boolean picked;

    public Appointment(User patient, String time, boolean picked) {
        this.patient = patient;
        this.time = time;
        this.picked = picked;
    }

    public Appointment(boolean picked) {
        this.picked = picked;
    }

    protected Appointment(Parcel in) {
        patient = in.readParcelable(User.class.getClassLoader());
        time = in.readString();
        picked = in.readByte() != 0;
    }

    public static final Creator<Appointment> CREATOR = new Creator<Appointment>() {
        @Override
        public Appointment createFromParcel(Parcel in) {
            return new Appointment(in);
        }

        @Override
        public Appointment[] newArray(int size) {
            return new Appointment[size];
        }
    };

    public User getPatient() {
        return patient;
    }

    public void setPatient(User patient) {
        this.patient = patient;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public boolean isPicked() {
        return picked;
    }

    public void setPicked(boolean picked) {
        this.picked = picked;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(patient, flags);
        dest.writeString(time);
        dest.writeByte((byte) (picked ? 1 : 0));
    }
}
