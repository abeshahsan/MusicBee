package com.musicbee.entities;

public class Playlist{
    private int ID;
    private String name;
    private String username;

    public Playlist(){
        this.ID = 0;
        this.name = "";
        this.username="";
    }
    public Playlist(int ID, String name,String username){
        this.ID = ID;
        this.name = name;
        this.username=username;
    }
    public void setID(int ID){
        this.ID = ID;
    }
    public void setName(String name){
        this.name = name;
    }
    public int getID(){
        return this.ID;
    }
    public String getName(){
        return this.name;
    }

    public String getUsername() {
        return username;
    }
    public void setUsername(String username){
        this.username=username;
    }
}