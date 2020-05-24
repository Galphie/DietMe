package com.galphie.dietme;

import android.os.Parcel;
import android.os.Parcelable;

public class Measures implements Parcelable {
    private double height; //m
    private double weight, leanMass, fatMass; //kg
    private int waist,hip,thigh,arm; //cm
    private int subscapularisFold, abdominalFold,
            suprailiacalFold, tricipitalFold, bicipitalFold; //mm

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

    public int getWaist() {
        return waist;
    }

    public void setWaist(int waist) {
        this.waist = waist;
    }

    public int getHip() {
        return hip;
    }

    public void setHip(int hip) {
        this.hip = hip;
    }

    public int getThigh() {
        return thigh;
    }

    public void setThigh(int thigh) {
        this.thigh = thigh;
    }

    public int getArm() {
        return arm;
    }

    public void setArm(int arm) {
        this.arm = arm;
    }

    public int getSubscapularisFold() {
        return subscapularisFold;
    }

    public void setSubscapularisFold(int subscapularisFold) {
        this.subscapularisFold = subscapularisFold;
    }

    public int getAbdominalFold() {
        return abdominalFold;
    }

    public void setAbdominalFold(int abdominalFold) {
        this.abdominalFold = abdominalFold;
    }

    public int getSuprailiacalFold() {
        return suprailiacalFold;
    }

    public void setSuprailiacalFold(int suprailiacalFold) {
        this.suprailiacalFold = suprailiacalFold;
    }

    public int getTricipitalFold() {
        return tricipitalFold;
    }

    public void setTricipitalFold(int tricipitalFold) {
        this.tricipitalFold = tricipitalFold;
    }

    public int getBicipitalFold() {
        return bicipitalFold;
    }

    public void setBicipitalFold(int bicipitalFold) {
        this.bicipitalFold = bicipitalFold;
    }

    public Measures() {
    }

    public Measures(double height, double weight, double leanMass, double fatMass, int waist,
                    int hip, int thigh, int arm, int subscapularisFold, int abdominalFold,
                    int suprailiacalFold, int tricipitalFold, int bicipitalFold) {
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
        waist = in.readInt();
        hip = in.readInt();
        thigh = in.readInt();
        arm = in.readInt();
        subscapularisFold = in.readInt();
        abdominalFold = in.readInt();
        suprailiacalFold = in.readInt();
        tricipitalFold = in.readInt();
        bicipitalFold = in.readInt();
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
        dest.writeInt(waist);
        dest.writeInt(hip);
        dest.writeInt(thigh);
        dest.writeInt(arm);
        dest.writeInt(subscapularisFold);
        dest.writeInt(abdominalFold);
        dest.writeInt(suprailiacalFold);
        dest.writeInt(tricipitalFold);
        dest.writeInt(bicipitalFold);
    }
}
