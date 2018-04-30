package com.groupapp.groupapp.groupapp.model;

import java.util.ArrayList;


public class Group {

    private String name;
    private String _id;
    private ArrayList<String> members;
    private ArrayList<String> conversation;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<String> getMembers() {
        return members;
    }

    public void addMembers(String newMember) {
        this.members.add(newMember);
    }

    public ArrayList<String> getConversation() {
        return conversation;
    }

    public void setConversation(ArrayList<String> conversation) {
        this.conversation = conversation;
    }

    public String getId() {
        return _id;
    }
}
