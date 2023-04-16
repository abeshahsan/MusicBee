package com.musicbee.utility;

import com.musicbee.entities.Song;

import java.util.ArrayList;

public class State {

    private static double volume = .5;
    private static double totalTime = 0;

    private static String currentSongName = "";
    private static String currentSongArtist = "";

    private static int lastSongIndex;
    private static double lastTimeStamp;
    private static int currentSongIndex;

    private static int currentPlaylistID;

    public static boolean mouseDetected = false;

    private static ArrayList<Song> songsInTable = new ArrayList<>();
    private static String currentPlaylistName=new String();
    private static int burgerState=-1;
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

        if(songsInTable == null) return;

        if(!songsInTable.isEmpty()) {
            songsInTable.clear();
        }
        songsInTable.addAll(list);
    }

    public static void setTotalTime(double time) {
        totalTime = time;
    }
    public static double getTotalTime() {
        return totalTime;
    }

    public static ArrayList<Song> getSongsInTable() {
        return songsInTable;
    }

    public static void clearState() {
        totalTime = 0;
        currentSongIndex = 0;
        songsInTable.clear();
    }
    public static void setLastSongID(int index) {lastSongIndex = index;}
    public static void setLastTimeStamp(double time) {lastTimeStamp = time;}

    public static int getLastSongID() {return lastSongIndex;}

    public static double getLastTimeStamp() {return lastTimeStamp;}
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
}
