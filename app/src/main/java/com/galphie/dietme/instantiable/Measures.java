package com.galphie.dietme.instantiable;

import android.os.Parcel;
import android.os.Parcelable;

public class Measures implements Parcelable {
    private double height; //m
    private double weight, leanMass, fatMass; //kg
    private double waist, hip, thigh, arm; //cm
    private double subscapularisFold, abdominalFold, suprailiacalFold, tricipitalFold, bicipitalFold; //mm

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public double getLeanMass() {
        return leanMass;
    }

    public void setLeanMass(double leanMass) {
        this.leanMass = leanMass;
    }

    public double getFatMass() {
        return fatMass;
    }

    public void setFatMass(double fatMass) {
        this.fatMass = fatMass;
    }

    public double getWaist() {
        return waist;
    }

    public void setWaist(double waist) {
        this.waist = waist;
    }

    public double getHip() {
        return hip;
    }

    public void setHip(double hip) {
        this.hip = hip;
    }

    public double getThigh() {
        return thigh;
    }

    public void setThigh(double thigh) {
        this.thigh = thigh;
    }

    public double getArm() {
        return arm;
    }

    public void setArm(double arm) {
        this.arm = arm;
    }

    public double getSubscapularisFold() {
        return subscapularisFold;
    }

    public void setSubscapularisFold(double subscapularisFold) {
        this.subscapularisFold = subscapularisFold;
    }

    public double getAbdominalFold() {
        return abdominalFold;
    }

    public void setAbdominalFold(double abdominalFold) {
        this.abdominalFold = abdominalFold;
    }

    public double getSuprailiacalFold() {
        return suprailiacalFold;
    }

    public void setSuprailiacalFold(double suprailiacalFold) {
        this.suprailiacalFold = suprailiacalFold;
    }

    public double getTricipitalFold() {
        return tricipitalFold;
    }

    public void setTricipitalFold(double tricipitalFold) {
        this.tricipitalFold = tricipitalFold;
    }

    public double getBicipitalFold() {
        return bicipitalFold;
    }

    public void setBicipitalFold(double bicipitalFold) {
        this.bicipitalFold = bicipitalFold;
    }

    public Measures() {
    }

    public Measures(boolean empty) {
        if (empty){
            this.height = 0;
            this.weight = 0;
            this.leanMass = 0;
            this.fatMass = 0;
            this.waist = 0;
            this.hip = 0;
            this.thigh = 0;
            this.arm = 0;
            this.subscapularisFold = 0;
            this.abdominalFold = 0;
            this.suprailiacalFold = 0;
            this.tricipitalFold = 0;
            this.bicipitalFold = 0;
        }
    }

    public Measures(double height, double weight, double leanMass, double fatMass, double waist,
                    double hip, double thigh, double arm, double subscapularisFold, double abdominalFold,
                    double suprailiacalFold, double tricipitalFold, double bicipitalFold) {
        this.height = height;
        this.weight = weight;
        this.leanMass = leanMass;
        this.fatMass = fatMass;
        this.waist = waist;
        this.hip = hip;
        this.thigh = thigh;
        this.arm = arm;
        this.subscapularisFold = subscapularisFold;
        this.abdominalFold = abdominalFold;
        this.suprailiacalFold = suprailiacalFold;
        this.tricipitalFold = tricipitalFold;
        this.bicipitalFold = bicipitalFold;
    }

    @Override
    public int describeContents() {
        return 0;
    }


    protected Measures(Parcel in) {
        height = in.readDouble();
        weight = in.readDouble();
        leanMass = in.readDouble();
        fatMass = in.readDouble();
        waist = in.readDouble();
        hip = in.readDouble();
        thigh = in.readDouble();
        arm = in.readDouble();
        subscapularisFold = in.readDouble();
        abdominalFold = in.readDouble();
        suprailiacalFold = in.readDouble();
        tricipitalFold = in.readDouble();
        bicipitalFold = in.readDouble();
    }

    public static final Creator<Measures> CREATOR = new Creator<Measures>() {
        @Override
        public Measures createFromParcel(Parcel in) {
            return new Measures(in);
        }

        @Override
        public Measures[] newArray(int size) {
            return new Measures[size];
        }
    };

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(height);
        dest.writeDouble(weight);
        dest.writeDouble(leanMass);
        dest.writeDouble(fatMass);
        dest.writeDouble(waist);
        dest.writeDouble(hip);
        dest.writeDouble(thigh);
        dest.writeDouble(arm);
        dest.writeDouble(subscapularisFold);
        dest.writeDouble(abdominalFold);
        dest.writeDouble(suprailiacalFold);
        dest.writeDouble(tricipitalFold);
        dest.writeDouble(bicipitalFold);
    }
}
