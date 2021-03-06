package com.groupapp.groupapp.groupapp.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;


public class User implements Parcelable {
    private String _id;
    private String name;
    private String private_key;
    private String created_at;
    private ArrayList<Group> groups;
    private String token;
    private String fcm_token;
    private String color;

    protected User(Parcel in) {
        _id = in.readString();
        name = in.readString();
        private_key = in.readString();
        created_at = in.readString();
        token = in.readString();
        fcm_token = in.readString();
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

    public void setToken(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public String getPrivate_key() {
        return private_key;
    }

    public void setPrivate_key(String private_key) {
        this.private_key = private_key;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getFcm_token() {
        return fcm_token;
    }

    public void setFcm_token(String fcm_token) {
        this.fcm_token = fcm_token;
    }

    public ArrayList<Group> getGroups() {
        return groups;
    }

    public void setGroups(ArrayList<Group> groups) {
        this.groups = groups;
    }

    public User(String name) {
        this.name = name;
//        this.private_key = private_key;
//        this.created_at = created_at;
//        this.groups = groups;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(_id);
        dest.writeString(name);
        dest.writeString(private_key);
        dest.writeString(created_at);
        dest.writeList(groups);
        dest.writeString(token);
        dest.writeString(fcm_token);
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }
}
