package com.example.hardapp;

import android.os.Parcel;
import android.os.Parcelable;

public class User implements Parcelable {

    private String email;
    private String pass;
    private String phone;
    private String profileUri;
    private String name;

    public User() {

    }

    public User(String email, String pass, String phone, String profileUri) {
        this.email = email;
        this.pass = pass;
        this.phone = phone;
        this.profileUri = profileUri;
    }

    protected User(Parcel in) {
        email = in.readString();
        pass = in.readString();
        phone = in.readString();
        profileUri = in.readString();
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




    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getProfileUri() {
        return profileUri;
    }

    public void setProfileUri(String profileUri) {
        this.profileUri = profileUri;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(email);
        dest.writeString(pass);
        dest.writeString(phone);
        dest.writeString(profileUri);
    }
}
