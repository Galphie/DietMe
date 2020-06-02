package com.galphie.dietme.instantiable;

import android.os.Parcel;
import android.os.Parcelable;

public class Signing implements Parcelable {

    private User user;
    private Appointment appointment;

    protected Signing(Parcel in) {
        user = in.readParcelable(User.class.getClassLoader());
        appointment = in.readParcelable(Appointment.class.getClassLoader());
    }

    public static final Creator<Signing> CREATOR = new Creator<Signing>() {
        @Override
        public Signing createFromParcel(Parcel in) {
            return new Signing(in);
        }

        @Override
        public Signing[] newArray(int size) {
            return new Signing[size];
        }
    };

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Appointment getAppointment() {
        return appointment;
    }

    public void setAppointment(Appointment appointment) {
        this.appointment = appointment;
    }


    public Signing(User user, Appointment appointment) {
        this.user = user;
        this.appointment = appointment;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(user, flags);
        dest.writeParcelable(appointment, flags);
    }
}
