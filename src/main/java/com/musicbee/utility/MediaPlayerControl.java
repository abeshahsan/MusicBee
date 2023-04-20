package com.musicbee.utility;

import com.musicbee.entities.Song;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.io.File;

public class MediaPlayerControl {
    private static Media media;
    private static MediaPlayer mediaPlayer;

    private static Song nowPlaying;

    static {
        media = null;
        mediaPlayer = null;
    }
    public static void play() {
        mediaPlayer.play();
    }
    public static void prepareJukebox(Song song) {
        nowPlaying = song;

        //Instantiating Media class
        media = new Media(new File(nowPlaying.getPath()).toURI().toString());

        if(mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.dispose();
        }

        //Instantiating MediaPlayer class
        mediaPlayer = new MediaPlayer(media);
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
    public static void clear() {
        if(mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.dispose();
            mediaPlayer = null;
        }
    }
}
