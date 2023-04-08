package com.musicbee.entities;

import javafx.scene.image.Image;

import java.util.Date;

public class User{
    private String firstName;
    private String lastName;
    private String username;
    private String email;
    private long password;
    private Date joinDate;

    private Image image = null;
    public User(){
        this.username = "";
        this.firstName = "";
        this.lastName = "";
        this.password = 0;
        this.email="";
    }
    public User(User user){
        this.username = user.username;
        this.password = user.password;
        this.firstName = user.firstName;
        this.lastName = user.lastName;
        this.joinDate = user.joinDate;
        this.email=user.email;
        this.image = user.image;
    }
    public User(String username, long password){
        this.username = username;
        this.password = password;
        this.firstName = "";
        this.lastName = "";
        this.email= "";
    }
    public void setFirstName(String firstName){
        if(firstName != null) this.firstName = firstName;
    }
    public void setLastName(String lastName){
        if(lastName != null) this.lastName = lastName;
    }
    public String getFirstName(){
        return this.firstName;
    }
    public String getLastName(){
        return this.lastName;
    }
    public String getName(){
        return this.firstName + " " + this.lastName;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public String getUsername() {
        return this.username;
    }

    public void setEmail(String email) {
        this.email = email;
    }
    public String getEmail() {
        return this.email;
    }

    public long getPassword(){
        return this.password;
    }
    public void setPassword(long password){
        this.password = password;
    }

    public void setJoinDate(Date date) {
        this.joinDate = date;
    }
    public Date getJoinDate() {
        return this.joinDate;
    }
     public void showInfo() {
        System.out.println("Username: " + this.username);
        System.out.println("Name: " + this.getName());
        System.out.println("Email: " + this.getEmail());
        if(this.joinDate != null) System.out.println("Joined since: " + this.joinDate.toString());
    }

    public void setImage(Image image) {
        this.image = image;
    }
    //get image 
    public Image getImage() {
        return this.image;
    }
}
