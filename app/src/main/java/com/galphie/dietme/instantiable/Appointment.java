package com.galphie.dietme.instantiable;

import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;

public class Appointment implements Parcelable {

    private String patientId;
    private boolean picked;

    public Appointment(String patientId, boolean picked) {
        this.patientId = patientId;
        this.picked = picked;
    }

    public Appointment(boolean picked) {
        this.picked = picked;
    }

    public Appointment() {
    }

    protected Appointment(Parcel in) {
        patientId = in.readString();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            picked = in.readBoolean();
        }
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
        dest.writeString(patientId);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            dest.writeBoolean(picked);
        }
    }
}
