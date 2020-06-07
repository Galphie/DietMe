package com.galphie.dietme.instantiable;

import android.os.Parcel;
import android.os.Parcelable;

public class Appointment implements Parcelable {

    private String patientId;
    private String time;
    private String date;
    private boolean picked;

    public Appointment() {
    }

    public Appointment(String time, boolean picked) {
        this.time = time;
        this.picked = picked;
    }

    public Appointment(String patientId, String time, boolean picked) {
        this.patientId = patientId;
        this.time = time;
        this.picked = picked;
    }

    public Appointment(String patientId, String time, String date, boolean picked) {
        this.patientId = patientId;
        this.time = time;
        this.date = date;
        this.picked = picked;
    }

    protected Appointment(Parcel in) {
        patientId = in.readString();
        time = in.readString();
        date = in.readString();
        picked = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(patientId);
        dest.writeString(time);
        dest.writeString(date);
        dest.writeByte((byte) (picked ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
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

    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public boolean isPicked() {
        return picked;
    }

    public void setPicked(boolean picked) {
        this.picked = picked;
    }


}

