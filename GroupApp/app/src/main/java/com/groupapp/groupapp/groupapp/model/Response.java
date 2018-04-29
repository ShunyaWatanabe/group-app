package com.groupapp.groupapp.groupapp.model;

import android.util.Log;

import java.util.ArrayList;

/**
 * Created by Tomek on 2018-04-04.
 */

public class Response {

    private String message;
    private String token;
    private String refresh_token;
    private String[] member_names;
    private String id;
    private String private_key;
    private Group[] groups;

    public String getMessage() {
        return message;
    }

    public String getToken() {
        return token;
    }

    public String getRefreshToken(){
        return refresh_token;
    }

    public String getId() {
        return id;
    }

    public String getPrivate_key() {
        return private_key;
    }

    public Group[] getGroups(){
        return groups;
    }

    public String[] getMembers(){return member_names;}

}