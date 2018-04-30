package com.groupapp.groupapp.groupapp.model;

/**
 * Created by Tomek on 2018-04-24.
 */

public class ConnectingUser{
    String name;
    String endpoint;
    String key;


    public ConnectingUser(String name, String endpoint){
        this.name = name;
        this.endpoint = endpoint;
        this.key = null; //this will be a user's id
    }

//    public ConnectingUser(String name, String endpoint){
//        this(name, endpoint,null);
//    }



    public String getName() {
        return name;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
