package com.musicbee.utility;

public class State {
    private static int lastSongID;

    private static int currentPlaylistID;

    public static boolean mouseDetected = false;

    private static String currentPlaylistName = "";
    private static int burgerState = -1;

    public static void setLastSongID(int ID) {
        lastSongID = ID;
    }

    public static int getLastSongID() {return lastSongID;}

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

    public static boolean isMouseDetected() {
        return mouseDetected;
    }

    public static void setMouseDetected(boolean mouseDetected) {
        State.mouseDetected = mouseDetected;
    }
}
