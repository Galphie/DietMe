package com.galphie.dietme.instantiable;

import android.os.Parcel;
import android.os.Parcelable;

public class Post implements Parcelable {

    private String message, publishDate;

    public Post(String message, String publishDate) {
        this.message = message;
        this.publishDate = publishDate;
    }

    public Post() {
    }

    protected Post(Parcel in) {
        message = in.readString();
        publishDate = in.readString();
    }

    public static final Creator<Post> CREATOR = new Creator<Post>() {
        @Override
        public Post createFromParcel(Parcel in) {
            return new Post(in);
        }

        @Override
        public Post[] newArray(int size) {
            return new Post[size];
        }
    };

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getPublishDate() {
        return publishDate;
    }

    public void setPublishDate(String publishDate) {
        this.publishDate = publishDate;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(message);
        dest.writeString(publishDate);
    }
}
