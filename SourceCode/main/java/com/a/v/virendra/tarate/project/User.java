package com.a.v.virendra.tarate.project;

public class User {

    private String username;
    private String email;
    //here Profile Picture Is an URL
    private String profilePicture;
    private String myphone;

    //An Empty Constructor for Firebase
    public User() {

    }


    //constructor


    public User(String username, String email, String profilePicture, String myphone) {
        this.username = username;
        this.email = email;
        this.profilePicture = profilePicture;
        this.myphone = myphone;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }

    public String getMyphone() {
        return myphone;
    }

    public void setMyphone(String myphone) {
        this.myphone = myphone;
    }
}