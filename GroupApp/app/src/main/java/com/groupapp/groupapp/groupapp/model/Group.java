package com.groupapp.groupapp.groupapp.model;

import java.util.ArrayList;


public class Group {

    private String name;
    private String _id;
    private ArrayList<User> members;
    private ArrayList<Message> conversation;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<User> getMembers() {
        return members;
    }

    public void addMembers(User newMember) {
        this.members.add(newMember);
    }

    public ArrayList<Message> getConversation() {
        return conversation;
    }

    public void setConversation(ArrayList<Message> conversation) {
        this.conversation = conversation;
    }

    public String getId() {
        return _id;
    }
}
