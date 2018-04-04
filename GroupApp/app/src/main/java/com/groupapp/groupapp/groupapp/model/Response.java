package com.groupapp.groupapp.groupapp.model;

/**
 * Created by Tomek on 2018-04-04.
 */

public class Response {

    private String message;
    private String token;
    private String refresh_token;
    private Boolean is_registered; //is user (fb) already registered
    private Boolean isAvailable; //is email available
    private String id;

    public String getMessage() {
        return message;
    }

    public String getToken() {
        return token;
    }

    public String getRefreshToken(){
        return refresh_token;
    }

    public Boolean getIsRegistered(){
        return is_registered;
    }

    public Boolean getIsAvailable(){
        return isAvailable;
    }

    public String getId() {
        return id;
    }

}