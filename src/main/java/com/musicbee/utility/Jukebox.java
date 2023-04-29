package com.musicbee.utility;

import com.musicbee.entities.Song;
import javafx.collections.ObservableList;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

public class Jukebox {
    public static final  ArrayList<Integer> shuffler      = new ArrayList<>();
    private static final ArrayList<Song>    currentList   = new ArrayList<>();
    private static       double             playbackPos;
    private static       double             totalDuration = 0;
    private static       double             volume        = .2;
    private static       int                currentSongIndex;
    private static       int                shufflerIndex = 0;
    private static       Media              media         = null;
    private static       MediaPlayer        mediaPlayer   = null;
    private static       Song               nowPlaying;


    public static void play() {
        mediaPlayer.play();
    }

    public static void setSong(Song song) {
        nowPlaying = song;
    }

    public static void prepare() {
        //Instantiating Media class
        media = new Media(new File(nowPlaying.getPath()).toURI().toString());

        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.dispose();
        }

        //Instantiating MediaPlayer class
        mediaPlayer = new MediaPlayer(media);
        mediaPlayer.setVolume(volume);
    }

    public static Media getMedia() {
        return media;
    }

    public static MediaPlayer getMediaPlayer() {
        return mediaPlayer;
    }

    public static Song getNowPlaying() {
        return nowPlaying;
    }

    public static void setNowPlaying(Song nowPlaying) {
        Jukebox.nowPlaying = nowPlaying;
    }

    public static void clear() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.dispose();
            mediaPlayer = null;
        }
    }

    public static double getPlaybackPos() {
        return playbackPos;
    }

    public static void setPlaybackPos(double time) {
        playbackPos = time;
    }

    public static double getTotalDuration() {
        return totalDuration;
    }

    public static void setTotalDuration(double time) {
        totalDuration = time;
    }

    public static double getVolume() {
        return volume;
    }

    public static void setVolume(double volume) {
        Jukebox.volume = volume;
    }

    public static ArrayList<Song> getCurrentList() {
        return currentList;
    }

    public static void setCurrentList(ObservableList<Song> list) {
        currentList.clear();
        currentList.addAll(list);
        shuffler.clear();
        for (int i = 0; i < currentList.size(); i++) {
            shuffler.add(i);
        }
        shufflerIndex = 0;
    }

    private static void shuffle() {
        Collections.shuffle(shuffler);
    }

    private static void undoShuffle() {
        for (int i = 0; i < shuffler.size(); i++) {
            shuffler.set(i, i);
        }
        shufflerIndex = currentSongIndex;
    }

    public static void setShuffle(boolean shuffle) {
        if (shuffle) {
            Jukebox.shuffle();
        } else {
            Jukebox.undoShuffle();
        }
    }

    public static int getCurrentSongIndex() {
        return currentSongIndex;
    }

    public static void setCurrentSongIndex(int index) {
        currentSongIndex = index;
    }

    public static void clearState() {
        totalDuration = 0;
        currentSongIndex = 0;
        currentList.clear();
    }

    public static boolean isNext() {
        return shufflerIndex + 1 < currentList.size();
    }

    public static boolean isPrev() {
        return shufflerIndex - 1 >= 0;
    }

    public static void next() {
        shufflerIndex++;
        currentSongIndex = shuffler.get(shufflerIndex);
        nowPlaying = currentList.get(currentSongIndex);
    }

    public static void prev() {
        shufflerIndex--;
        currentSongIndex = shuffler.get(shufflerIndex);
        nowPlaying = currentList.get(currentSongIndex);
    }
}
