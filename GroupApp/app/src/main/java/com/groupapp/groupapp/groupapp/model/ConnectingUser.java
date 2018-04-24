package com.groupapp.groupapp.groupapp.model;

/**
 * Created by Tomek on 2018-04-24.
 */

public class ConnectingUser{
    String name;
    String endpoint;

    public ConnectingUser(String name, String endpoint){
        this.name = name;
        this.endpoint = endpoint;
    }

    public String getName() {
        return name;
    }

    public String getEndpoint() {
        return endpoint;
    }
}
