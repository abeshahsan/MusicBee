package com.musicbee.utility;

import com.musicbee.entities.Song;

import java.util.ArrayList;

public class State {

    private static double volume = .2;
    private static double totalDuration = 0;

    private static String currentSongName = "";
    private static String currentSongArtist = "";

    private static int lastSongIndex;
    private static double playbackPos;
    private static int currentSongIndex;

    private static int currentPlaylistID;

    public static boolean mouseDetected = false;

    private static final ArrayList<Song> songsInTable = new ArrayList<>();
    private static String currentPlaylistName = "";
    private static int burgerState = -1;
    public static void setVolume(double volume){
        State.volume = volume;
    }
    public static double getVolume() {
        return volume;
    }
    public static void setCurrentSongIndex(int index){
        State.currentSongIndex = index;
    }
    public static void incrementCurrentSongIndex(){
        if(currentSongIndex + 1 < songsInTable.size()) {
            currentSongIndex++;
        }
    }
    public static void decrementCurrentSongIndex(){
        if(currentSongIndex - 1 >= 0) {
            currentSongIndex--;
        }
    }
    public static int getCurrentSongIndex() {
        return currentSongIndex;
    }
    public static void setSongsInTable(ArrayList<Song> list) {
        if(!songsInTable.isEmpty()) {
            songsInTable.clear();
        }
        songsInTable.addAll(list);
    }

    public static void setTotalDuration(double time) {
        totalDuration = time;
    }
    public static double getTotalDuration() {
        return totalDuration;
    }

    public static ArrayList<Song> getSongsInTable() {
        return songsInTable;
    }

    public static void clearState() {
        totalDuration = 0;
        currentSongIndex = 0;
        songsInTable.clear();
    }
    public static void setLastSongID(int index) {lastSongIndex = index;}
    public static void setPlaybackPos(double time) {
        playbackPos = time;}

    public static int getLastSongID() {return lastSongIndex;}

    public static double getPlaybackPos() {return playbackPos;}
    public static void setCurrentPlaylistID(int pLID){
        currentPlaylistID=pLID;
    }
    public static int getCurrentPlaylistID(){
        return currentPlaylistID;
    }
    public static void setBurgerState(int rate){burgerState=rate;}
    public static int getBurgerState(){return burgerState; }

    public static String getCurrentPlaylistName() {
        return currentPlaylistName;
    }

    public static void setCurrentPlaylistName(String currentPlaylistName) {
        State.currentPlaylistName = currentPlaylistName;
    }

    public static void setCurrentSongName(String currentSongName) {
        State.currentSongName = currentSongName;
    }

    public static void setCurrentSongArtist(String currentSongArtist) {
        State.currentSongArtist = currentSongArtist;
    }

    public static String getCurrentSongName() {
        return currentSongName;
    }

    public static String getCurrentSongArtist() {
        return currentSongArtist;
    }

    public static boolean isMouseDetected() {
        return mouseDetected;
    }

    public static void setMouseDetected(boolean mouseDetected) {
        State.mouseDetected = mouseDetected;
    }
}
