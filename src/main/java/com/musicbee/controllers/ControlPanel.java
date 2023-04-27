package com.musicbee.controllers;

import com.jfoenix.controls.JFXSlider;
import com.musicbee.entities.Song;
import com.musicbee.utility.FilePaths;
import com.musicbee.utility.MediaPlayerControl;
import com.musicbee.utility.State;
import com.musicbee.utility.Tools;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

public class ControlPanel implements Initializable {
    @FXML
    private Button next;
    @FXML
    private Button prev;
    @FXML
    private JFXSlider timeSlider;
    @FXML
    private JFXSlider volumeSlider;
    @FXML
    private Button playPause;
    @FXML
    private Label artistName;
    @FXML
    private Label songName;
    @FXML
    private Label elapsed;
    @FXML
    private Label   totalDuration;
    @FXML
    private Button  volumeIndicator;
    private boolean isMuted;
    private double volSliderValue;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        setTimeSlider();

        System.out.println(State.getVolume());

        if(State.getTotalDuration() != 0) {
            timeSlider.setValue(  (State.getPlaybackPos() / State.getTotalDuration()) * 100.0 );
        }

        songName.setText(State.getCurrentSongName());
        artistName.setText(State.getCurrentSongArtist());
        totalDuration.setText(Tools.timeToString(State.getTotalDuration()));
        elapsed.setText(Tools.timeToString(State.getPlaybackPos()));

        isMuted = false;

        setVolumeSlider();
        setVolumeIcon();

        if(MediaPlayerControl.getMediaPlayer() != null
                && MediaPlayerControl.getMediaPlayer().getStatus() == MediaPlayer.Status.PLAYING) {
            setPause();
        }
        else setPlay();
        setPrev();
        setNext();
        setTimeSliderEventHandlers();
    }

    private void setVolumeSlider() {
        volumeSlider.setValue(State.getVolume() * 100);
        volumeSlider.valueProperty().addListener((observableValue, number, t1) -> {
            State.setVolume(volumeSlider.getValue() * .01);
            if(MediaPlayerControl.getMediaPlayer() != null) {
                MediaPlayerControl.getMediaPlayer().setVolume(State.getVolume());
            }
            setVolumeIcon();
        });
    }

    private void setTimeSliderEventHandlers() {
        timeSlider.setOnMouseDragged(event -> State.mouseDetected = true);
        timeSlider.setOnMousePressed(event -> State.mouseDetected = true);
        timeSlider.setOnMouseReleased(event -> {
            if(MediaPlayerControl.getMediaPlayer() == null) {
                timeSlider.setValue(0);
            }
            else {
                double seekTime = (timeSlider.getValue() / 100) * State.getTotalDuration();
                MediaPlayerControl.getMediaPlayer().seek(Duration.millis(seekTime));
            }
            State.mouseDetected = false;
        });
    }

    @FXML
    private void onClickNext() {
        playNext();
    }

    @FXML
    private void onClickPlayPause() {
        MediaPlayer mediaPlayer = MediaPlayerControl.getMediaPlayer();
        if(mediaPlayer != null) {
            if (mediaPlayer.getStatus() == MediaPlayer.Status.PLAYING) {
                mediaPlayer.pause();
                setPlay();
            }
            else if(mediaPlayer.getStatus() == MediaPlayer.Status.PAUSED){
                mediaPlayer.play();
               setPause();
            }
        }
    }

    @FXML
    private void onClickPrev() {
        MediaPlayer player = MediaPlayerControl.getMediaPlayer();
        if(player != null && (State.getCurrentSongIndex() - 1 >= 0) ) {
            State.decrementCurrentSongIndex();
            State.setLastSongID(State.getSongsInTable().get(State.getCurrentSongIndex()).getID());
            player.stop();
            player.dispose();
            Song song = State.getSongsInTable().get(State.getCurrentSongIndex());
            MediaPlayerControl.prepare(song);
            MediaPlayerControl.play();
            setTimeSlider();
            MediaPlayerControl.getMediaPlayer().setVolume(State.getVolume());
            State.setCurrentSongName(State.getSongsInTable().get(State.getCurrentSongIndex()).getName());
            State.setCurrentSongArtist(State.getSongsInTable().get(State.getCurrentSongIndex()).getArtistName());
            updateNames(State.getCurrentSongName(),
                    State.getCurrentSongArtist());
            setPlay();
        }
    }

    public void setTimeSlider() {
        timeSlider.setValue(0);
        if(MediaPlayerControl.getMediaPlayer() != null) {
            MediaPlayerControl.getMediaPlayer().currentTimeProperty().addListener((observableValue, duration, t1) -> {
                if(MediaPlayerControl.getMediaPlayer() == null) return;
                double totalTime = MediaPlayerControl.getMediaPlayer().getTotalDuration().toMillis();
                State.setTotalDuration(totalTime);
                double currentTime = MediaPlayerControl.getMediaPlayer().getCurrentTime().toMillis();
                State.setPlaybackPos(currentTime);
                double timeSliderValue = (currentTime / totalTime) * 100;
                if(!State.mouseDetected) timeSlider.setValue(timeSliderValue);
                totalDuration.setText(Tools.timeToString(totalTime));
                elapsed.setText(Tools.timeToString(currentTime));
            });
            MediaPlayerControl.getMediaPlayer().setOnEndOfMedia(this::playNext);
        }
    }

    private void updateNames(String name, String artist) {
        songName.setText(name);
        artistName.setText(artist);
    }

    public JFXSlider getTimeSlider() {
        return timeSlider;
    }

    public Label getSongName() {
        return songName;
    }

    public Label getArtistName() {
        return artistName;
    }

    public Button getPlayPause() {
        return playPause;
    }
    public void playNext() {
        if(MediaPlayerControl.getMediaPlayer() != null && (State.getCurrentSongIndex() + 1 < State.getSongsInTable().size()) ) {
            State.incrementCurrentSongIndex();
            State.setLastSongID(State.getSongsInTable().get(State.getCurrentSongIndex()).getID());
            Song song = State.getSongsInTable().get(State.getCurrentSongIndex());
            MediaPlayerControl.prepare(song);
            MediaPlayerControl.play();
            State.setCurrentSongName(State.getSongsInTable().get(State.getCurrentSongIndex()).getName());
            State.setCurrentSongArtist(State.getSongsInTable().get(State.getCurrentSongIndex()).getArtistName());
            updateNames(State.getCurrentSongName(),
                    State.getCurrentSongArtist());
            setTimeSlider();
            MediaPlayerControl.getMediaPlayer().setVolume(State.getVolume());
            setPause();
        }
    }

    public void setPlay() {
        try {
            File file = new File(FilePaths.PLAY_ICON);
            ImageView imageView = new ImageView(file.getAbsolutePath());
            imageView.setFitWidth(15);
            imageView.setFitHeight(20);
            playPause.setGraphic(imageView);
            playPause.getTooltip().setText("Play");
            playPause.getTooltip().setShowDelay(Duration.millis(100));
        } catch (Exception ignore) {
            playPause.setText("Play");
        }
    }

    public void setPause() {
        try {
            File file = new File(FilePaths.PAUSE_ICON);
            ImageView imageView = new ImageView(file.getAbsolutePath());
            imageView.setFitWidth(15);
            imageView.setFitHeight(20);
            playPause.setGraphic(imageView);
            playPause.getTooltip().setText("Pause");
            playPause.getTooltip().setShowDelay(Duration.millis(100));
        } catch (Exception ignore) {
            playPause.setText("Pause");
        }
    }

    private void setNext() {
        try {
            File file = new File(FilePaths.NEXT_ICON);
            ImageView imageView = new ImageView(file.getAbsolutePath());
            imageView.setFitWidth(15);
            imageView.setFitHeight(20);
            next.setGraphic(imageView);
            next.getTooltip().setShowDelay(Duration.millis(100));
        } catch (Exception ignore) {
            next.setText("Next");
        }
    }

    private void setPrev() {
        try {
            File file = new File(FilePaths.PREV_ICON);
            ImageView imageView = new ImageView(file.getAbsolutePath());
            imageView.setFitWidth(15);
            imageView.setFitHeight(20);
            prev.setGraphic(imageView);
            prev.getTooltip().setShowDelay(Duration.millis(100));
        } catch (Exception ignore) {
            prev.setText("Prev");
        }
    }
    @FXML
    private void toggleMute() {
        if(isMuted) {
            isMuted = false;
            volumeSlider.setValue(volSliderValue);
        } else {
            isMuted = true;
            volSliderValue = volumeSlider.getValue();
            volumeSlider.setValue(0);
        }
    }

    private void setVolumeIcon() {
        String volumeIndicatorIcon = "";
        if(0 <= volumeSlider.getValue() && volumeSlider.getValue() < 1) {
            volumeIndicatorIcon =  FilePaths.VOLUME_INDICATOR_MUTE;
        } else if (1 <= volumeSlider.getValue() && volumeSlider.getValue() < volumeSlider.getMax() * 1 / 3){
            volumeIndicatorIcon = FilePaths.VOLUME_INDICATOR_LOW;
        } else if (volumeSlider.getMax() * 1 / 3 <= volumeSlider.getValue() && volumeSlider.getValue() < volumeSlider.getMax() * 2 / 3){
            volumeIndicatorIcon = FilePaths.VOLUME_INDICATOR_MEDIUM;
        } else if (volumeSlider.getMax() * 2 / 3 <= volumeSlider.getValue() && volumeSlider.getValue() <= volumeSlider.getMax()){
            volumeIndicatorIcon = FilePaths.VOLUME_INDICATOR_HIGH;
        }
        File file = new File(volumeIndicatorIcon);
        ImageView imageView = new ImageView(file.getAbsolutePath());
        imageView.setFitWidth(30);
        imageView.setFitHeight(35);
        volumeIndicator.setGraphic(imageView);
    }
}
