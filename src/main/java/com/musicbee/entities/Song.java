package com.musicbee.entities;

import com.musicbee.utility.Tools;

public class Song {
    boolean isChanged;
    private int    ID;
    private String name;
    private String path;
    private double length;
    private String lengthInString;
    private String artistFirstName;
    private String artistLastName;
    private String artistName;
    private String albumName;


    public static final String NAME_ATTRIBUTE = "name";
    public static final String ARTIST_NAME_ATTRIBUTE = "artistName";
    public static final String ALBUM_NAME_ATTRIBUTE       = "albumName";
    public static final String LENGTH_IN_STRING_ATTRIBUTE = "lengthInString";

    public Song() {
        this.ID = 0;
        this.name = "";
        this.path = "";
        isChanged = false;
        artistFirstName = "";
        artistLastName = "";
        artistName = "";
        albumName = "";
    }

    public Song(int ID, String name, String path) {
        this.ID = ID;
        this.name = name;
        this.path = path;
        isChanged = false;
        artistFirstName = "";
        artistLastName = "";
        artistName = "";
        albumName = "";
    }

    public String getAlbumName() {
        return this.albumName;
    }

    public void setAlbumName(String albumName) {
        this.albumName = albumName;
    }

    public String getArtistName() {
        return this.artistName;
    }

    public String getArtistFirstName() {
        return this.artistFirstName;
    }

    public void setArtistFirstName(String artistFirstName) {
        if (artistFirstName != null) {
            this.artistFirstName = artistFirstName;
            artistName = artistFirstName;
        }

    }

    public String getArtistLastName() {
        return this.artistLastName;
    }

    public void setArtistLastName(String artistLastName) {
        if (artistLastName != null) {
            this.artistLastName = artistLastName;
            if (artistName != null) artistName = artistName + " " + artistLastName;
            else artistName = artistLastName;
        }
    }

    public String getPath() {
        return this.path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getID() {
        return this.ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getLength() {
        return length;
    }

    public void setLength(double length) {
        this.length = length;
        lengthInString = Tools.timeToString(length);
    }

    public String getLengthInString() {
        return lengthInString;
    }
}