package com.musicbee.utility;

import com.musicbee.entities.Song;
import javafx.collections.ObservableList;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.io.File;
import java.util.ArrayList;

public class Jukebox {
    private static final ArrayList<Song> currentList   = new ArrayList<>();

    private static       double          playbackPos;
    private static       double          totalDuration = 0;
    private static       double          volume        = .2;
    private static       int             currentSongIndex;
    private static       Media           media         = null;
    private static       MediaPlayer     mediaPlayer   = null;
    private static       Song            nowPlaying;


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
        return currentSongIndex + 1 < currentList.size();
    }
    public static boolean isPrev() {
        return currentSongIndex - 1 >= 0;
    }

    public static void next() {
        currentSongIndex++;
        nowPlaying = currentList.get(currentSongIndex);
    }

    public static void prev() {
        currentSongIndex--;
        nowPlaying = currentList.get(currentSongIndex);
    }
}
