package com.musicbee.entities;

public class Genre{
    private String ID;
    private String name;

    public Genre(){
        this.ID = "";
        this.name = "";
    }
    public Genre(String ID, String name){
        this.ID = ID;
        this.name = name;
    }
    public void setID(String ID){
        this.ID = ID;
    }
    public void setName(String n){
        this.name = n;
    }
    public String getID(){
        return this.ID;
    }
    public String getName(){
        return this.name;
    }
}