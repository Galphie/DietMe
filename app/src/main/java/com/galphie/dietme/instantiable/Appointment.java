package com.galphie.dietme.instantiable;

import android.os.Parcel;
import android.os.Parcelable;

public class Appointment implements Parcelable {

    private String name;
    private String time;

    public Appointment() {
    }

    public Appointment(String time, boolean picked) {
        this.time = time;
        this.picked = picked;
    }

    public Appointment(String name, String time, boolean picked) {
        this.name = name;
        this.time = time;
        this.picked = picked;
    }

    private boolean picked;

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }


    protected Appointment(Parcel in) {
        name = in.readString();
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
        dest.writeString(name);
        dest.writeByte((byte) (picked ? 1 : 0));
    }
}
