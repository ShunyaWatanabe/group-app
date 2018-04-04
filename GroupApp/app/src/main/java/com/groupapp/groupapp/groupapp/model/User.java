package com.groupapp.groupapp.groupapp.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by Tomek on 2018-04-04.
 */

public class User implements Parcelable {

    private String name;
    private String private_key;
    private String created_at;
    private ArrayList<Group> groups;

    protected User(Parcel in) {
        name = in.readString();
        private_key = in.readString();
        created_at = in.readString();
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

    public ArrayList<Group> getGroups() {
        return groups;
    }

    public void setGroups(ArrayList<Group> groups) {
        this.groups = groups;
    }

    public User(String name, String private_key, String created_at, ArrayList<Group> groups) {
        this.name = name;
        this.private_key = private_key;
        this.created_at = created_at;
        this.groups = groups;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(private_key);
        dest.writeString(created_at);
        dest.writeList(groups);
    }
}
