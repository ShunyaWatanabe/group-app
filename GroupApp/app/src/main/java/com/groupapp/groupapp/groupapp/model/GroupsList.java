package com.groupapp.groupapp.groupapp.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by Tomek on 2018-04-04.
 */

public class GroupsList {
    @SerializedName("groups")
private ArrayList<Group> groups;

    public GroupsList(){}

    public ArrayList<Group> getEvents(){return groups;}

    public void setEvents(ArrayList<Group> events){
        this.groups = groups;
    }

    @SerializedName("token")
    private String token;

    public void setToken(String token) {
        this.token = token;
    }

    public String getToken(){return token;}
}