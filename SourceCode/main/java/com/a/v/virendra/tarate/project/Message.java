// This Project is Created by Team Interstellars For Solving For India Hack-a-thon by Geeks for Geeks
// ©️ All Rights Reserved By Team Interstellars
package com.a.v.virendra.tarate.project;

public class Message {

    private String sender;
    private String reciver;
    private String contet;

    //empty constructor for firebase
    public Message(){

    }



    //geeters and setters


    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReciver() {
        return reciver;
    }

    public void setReciver(String reciver) {
        this.reciver = reciver;
    }

    public String getContet() {
        return contet;
    }

    public void setContet(String contet) {
        this.contet = contet;
    }


    //constructor
    public Message(String sender, String reciver, String contet) {
        this.sender = sender;
        this.reciver = reciver;
        this.contet = contet;
    }
}
