package com.musicbee.entities;

import com.musicbee.utility.State;
import com.musicbee.utility.Tools;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

import java.io.File;

public class Song{
    private static Media media;
    private static MediaPlayer mediaPlayer;
    private int ID;
    private String name;
    private String path;

    private double length;
    private String lengthInString;


    private String artistFirstName;
    private String artistLastName;
    boolean isChanged;
    private String artistName;
    private String albumName;

    static {
        media = null;
        mediaPlayer = null;
    }

    public Song(){
        this.ID = 0;
        this.name = "";
        this.path = "";
        isChanged = false;
        artistFirstName = "";
        artistLastName = "";
        artistName="";
        albumName="";
    }
    public Song(int ID, String name, String path){
        this.ID = ID;
        this.name = name;
        this.path = path;
        isChanged = false;
        artistFirstName = "";
        artistLastName = "";
        artistName="";
        albumName="";
    }
    public void setID(int ID){
        this.ID = ID;
    }
    public void setArtistFirstName(String artistFirstName){
        if(artistFirstName != null)
        {
            this.artistFirstName = artistFirstName;
            artistName=artistFirstName;
        }

    }
    public void setAlbumName(String albumName){this.albumName=albumName;}
    public String getAlbumName(){return this.albumName;}
    public void setArtistLastName(String artistLastName){
        if(artistLastName != null)
        {
            this.artistLastName = artistLastName;
            if(artistName!=null)artistName=artistName+" "+artistLastName;
            else artistName=artistLastName;
        }
    }
    public String getArtistName() {
        return this.artistName;
    }

    public String getArtistFirstName(){
        return this.artistFirstName;
    }
    public String getArtistLastName(){
        return this.artistLastName;
    }

//    public String getArtistName(){
//        return this.artistFirstName + " " + artistLastName;
//    }

    public void setName(String name){
        this.name = name;
    }
    public void setPath(String path){
        this.path = path;
    }
    public String getPath(){
        return this.path;
    }
    public int getID(){
        return this.ID;
    }
    public String getName(){
        return this.name;
    }
    public void play()
    {
        //Instantiating Media class
        media = new Media(new File(path).toURI().toString());

        if(mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.dispose();
        }

        //Instantiating MediaPlayer class
        mediaPlayer = new MediaPlayer(media);

        mediaPlayer.play();
    }

    public static MediaPlayer getMediaPlayer() {
        return mediaPlayer;
    }
    public static Media getMedia() {
        return media;
    }

    public static void cleanMediaPlayer() {
        if(mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.dispose();
            mediaPlayer = null;
        }
    }
    public void setLength(double length) {
        this.length = length;
        lengthInString = Tools.timeToString(length);
    }
    public double getLength() {
        return length;
    }

    public String getLengthInString() {
        return lengthInString;
    }
}