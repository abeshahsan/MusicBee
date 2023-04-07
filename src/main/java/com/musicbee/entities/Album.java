package com.musicbee.entities;

public class Album {
    private String ID;
    private String name;

    public Album(){
        this.ID = "";
        this.name = "";
    }
    public Album(String name, String ID) {
        this.ID = ID;
        this.name = name;
    }
    public void setID(String ID) {
        this.ID = ID;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getID() {
        return this.ID;
    }
    public String getName() {
        return this.name;
    }
}