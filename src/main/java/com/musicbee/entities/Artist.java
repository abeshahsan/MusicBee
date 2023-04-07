package com.musicbee.entities;

public class Artist{
    private String ID;
    private String firstName;
    private String lastName;

    public Artist(){
        this.ID = "";
        this.firstName = "";
        this.lastName = "";
    }
    public Artist(String ID, String firstName, String lastName){
        this.ID = ID;
        this.firstName = firstName;
        this.lastName = lastName;
    }
    public void setID(String ID){
        this.ID = ID;
    }
    public void setFirstName(String firstName){
        this.firstName = firstName;
    }
    public void setLastName(String lastName){
        this.lastName = lastName;
    }
    public String getFirstName(){
        return this.firstName;
    }
    public String getID(){
        return ID;
    }
    public String getLastName(){
        return this.lastName;
    }
    public String getName() {
        return this.firstName + " " + this.lastName;
    }
}