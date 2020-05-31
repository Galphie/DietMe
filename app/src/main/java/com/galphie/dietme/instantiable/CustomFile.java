package com.galphie.dietme.instantiable;

import android.os.Parcel;
import android.os.Parcelable;

public class CustomFile implements Parcelable {

    private String name;
    private String url;
    private String path;


    public CustomFile() {
    }

    public CustomFile(String name, String url) {
        this.name = name;
        this.url = url;
    }

    public CustomFile(String name, String url, String path) {
        this.name = name;
        this.url = url;
        this.path = path;
    }

    protected CustomFile(Parcel in) {
        name = in.readString();
        url = in.readString();
        path = in.readString();
    }

    public static final Creator<CustomFile> CREATOR = new Creator<CustomFile>() {
        @Override
        public CustomFile createFromParcel(Parcel in) {
            return new CustomFile(in);
        }

        @Override
        public CustomFile[] newArray(int size) {
            return new CustomFile[size];
        }
    };

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(url);
        dest.writeString(path);
    }
}
