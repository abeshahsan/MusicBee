package com.musicbee.utility;

import com.musicbee.entities.Song;
import javafx.collections.ObservableList;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

/**
 * The Jukebox class is to control the list of songs that is to be played.
 * <br> It can:
 * <ul>
 *     <li>Play a song</li>
 *     <li>Change a song on request/selection</li>
 *     <li>Play the next song on request, or after the current song ends</li>
 *     <li>Play the previous song on request</li>
 *     <li>Shuffle/Undo shuffle a playlist</li>
 * </ul>
 */
public class Jukebox {

    /**
     * This will hold the serial numbers, i.e, the indices of the songs in the playlist. The songs will be played as
     * the indices comes.
     * <p>
     * For example: If the integers in this list is : 3, 5, 1, 2; So at first the 3rd song will play, then 5th, and so on...
     * The size of this list will be the same as the original playlist.
     */
    public static final ArrayList<Integer> shuffler = new ArrayList<>();

    /**
     * This will hold the current playlist int the Jukebox. The songs will be in original order as appear on the interface.
     */
    private static final ArrayList<Song> currentList = new ArrayList<>();

    /**
     * The current playback position of the current song, in milliseconds.
     */
    private static double playbackPos;

    /**
     * The total duration of the current song, in milliseconds.
     */
    private static double totalDuration = 0;

    /**
     * The volume if the song, ranging from 0.0 to 1.0.
     */
    private static double volume = .2; //default value

    /**
     * The index of the current song in {@code currentList}.
     */
    private static int currentSongIndex;

    /**
     * The current index on the {@code shuffler} list.
     */
    private static int shufflerIndex = 0;

    /**
     * It is required to play any audio file. Whenever an audio file is to be played,
     * a new Media object should be created
     */
    private static MediaPlayer mediaPlayer = null;
    /**
     * The Media class is required to prepare the MediaPlayer. Every time the MediaPlayer object is changed,
     * It needs a new Media.
     */

    private static Media       media       = null;

    /**
     * Holds the information of the currently playing song.
     */
    private static Song nowPlaying;

    /**
     * {@code true} if the shuffle mode is on.
     */
    static boolean shuffleOn = false;


    /**
     * plays the current song
     */
    public static void play() {
        mediaPlayer.play();
    }


    /**
     * Changes the current song with a given input.
     *
     * @param song The input song
     */
    public static void setSong(Song song) {
        nowPlaying = song;
    }

    /**
     * Prepares the media player before playing the song.
     */
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

    /**
     * Returns the {@link Media} object used int the {@link Jukebox} to prepare the {@code MediaPlayer}.
     */

    public static Media getMedia() {
        return media;
    }

    /**
     * Returns the {@link MediaPlayer} object used in the {@link Jukebox} to play/pause the songs.
     */
    public static MediaPlayer getMediaPlayer() {
        return mediaPlayer;
    }

    /**
     * Returns the current song that is in the {@link Jukebox}
     */
    public static Song getNowPlaying() {
        return nowPlaying;
    }

    /**
     * Clears the MediaPlayer
     */
    public static void clearMediaPlayer() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.dispose();
            mediaPlayer = null;
        }
    }

    /**
     * Returns the current playback position of the current song, in milliseconds.
     */
    public static double getPlaybackPos() {
        return playbackPos;
    }

    /**
     * Sets the current playback position of the current song, in milliseconds.
     *
     * @param time - The current position
     */
    public static void setPlaybackPos(double time) {
        playbackPos = time;
    }

    /**
     * Returns the total duration of the current song, in milliseconds.
     */
    public static double getTotalDuration() {
        return totalDuration;
    }

    /**
     * Sets the total duration of the current song, in milliseconds.
     *
     * @param time - The total duration
     */
    public static void setTotalDuration(double time) {
        totalDuration = time;
    }

    /**
     * Returns the volume
     */
    public static double getVolume() {
        return volume;
    }

    /**
     * Sets the volume
     *
     * @param volume - The input volume
     */
    public static void setVolume(double volume) {
        Jukebox.volume = volume;
    }

    /**
     * Returns the current playlist in the Jukebox
     */
    public static ArrayList<Song> getCurrentList() {
        return currentList;
    }

    /**
     * Sets the current playlist of the Jukebox.
     *
     * @param list - The input playlist
     */
    public static void setCurrentList(ObservableList<Song> list, int selectedSongIndex) {
        currentList.clear();
        currentList.addAll(list);
        shuffler.clear();
        for (int i = 0; i < currentList.size(); i++) {
            shuffler.add(i);
        }
        setShuffle();
        shufflerIndex = 0;

        // Find the index of the current song from the shuffler arrayList.
        for (int i = 0; i < currentList.size(); i++) {
            if(shuffler.get(i) == selectedSongIndex) {
                Collections.swap(shuffler, 0, i);
                break;
            }
        }
    }

    /**
     * Shuffles of sorts the playlist based the shuffle-mode. If it's on, then it will shuffle, otherwise
     * it keep the playlist as it is being displayed on the interface.
     */
    public static void setShuffle() {
        if (shuffleOn) {
            Jukebox.shuffle();
        } else {
            Jukebox.undoShuffle();
        }
    }

    /**
     * Returns the current index of the song from the playlist.
     */
    public static int getCurrentSongIndex() {
        return currentSongIndex;
    }

    /**
     * Sets the current index of the song from the playlist, the selected song will be played.
     *
     * @param index The input index
     */
    public static void setCurrentSongIndex(int index) {
        currentSongIndex = index;
    }

    /**
     * Clears the Jukebox
     */
    public static void clear() {
        totalDuration = 0;
        currentSongIndex = 0;
        currentList.clear();
    }

    /**
     * Checks if there is any song after the current song
     *
     * @return true if there is any next song
     */
    public static boolean isNext() {
        return shufflerIndex + 1 < currentList.size();
    }

    /**
     * Checks if there is any song before the current song
     *
     * @return true if there is any previous song
     */
    public static boolean isPrev() {
        return shufflerIndex - 1 >= 0;
    }

    /**
     * Plays the next song
     */
    public static void next() {
        shufflerIndex++;
        currentSongIndex = shuffler.get(shufflerIndex);
        nowPlaying = currentList.get(currentSongIndex);
    }

    /**
     * Plays the previous song
     */
    public static void prev() {
        shufflerIndex--;
        currentSongIndex = shuffler.get(shufflerIndex);
        nowPlaying = currentList.get(currentSongIndex);
    }

    /**
     * Shuffles the current playlist in the Jukebox.
     */
    private static void shuffle() {
        Collections.shuffle(shuffler);
    }

    /**
     * Undoes the shuffle of the playlist in the Jukebox
     */
    private static void undoShuffle() {
        for (int i = 0; i < shuffler.size(); i++) {
            shuffler.set(i, i);
        }
        shufflerIndex = currentSongIndex;
    }

    public static boolean isShuffleOn() {
        return shuffleOn;
    }

    public static void setShuffleOn(boolean shuffleOn) {
        Jukebox.shuffleOn = shuffleOn;
    }
}
