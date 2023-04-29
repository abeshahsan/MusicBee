package com.musicbee.controllers;

import com.jfoenix.controls.JFXSlider;
import com.musicbee.entities.Song;
import com.musicbee.utility.FilePaths;
import com.musicbee.utility.Jukebox;
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
    private Button    shuffleIndicator;
    @FXML
    private Button    nextButton;
    @FXML
    private Button    prevButton;
    @FXML
    private JFXSlider timeSlider;
    @FXML
    private JFXSlider volumeSlider;
    @FXML
    private Button    playPauseButton;
    @FXML
    private Label     artistNameLabel;
    @FXML
    private Label     songNameLabel;
    @FXML
    private Label     elapsed;
    @FXML
    private Label     totalDuration;
    @FXML
    private Button    volumeIndicator;
    private boolean   isMuted;
    private double    volSliderValue;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        setTimeSlider();

        System.out.println(Jukebox.getVolume());

        if (Jukebox.getTotalDuration() != 0) {
            timeSlider.setValue((Jukebox.getPlaybackPos() / Jukebox.getTotalDuration()) * timeSlider.getMax());
        }

        songNameLabel.setText(Jukebox.getNowPlaying().getName());
        artistNameLabel.setText(Jukebox.getNowPlaying().getArtistName());
        totalDuration.setText(Tools.timeToString(Jukebox.getTotalDuration()));
        elapsed.setText(Tools.timeToString(Jukebox.getPlaybackPos()));

        isMuted = false;

        setVolumeSlider();
        setVolumeIndicatorButton();

        if (Jukebox.getMediaPlayer() != null
                && Jukebox.getMediaPlayer().getStatus() == MediaPlayer.Status.PLAYING) {
            setPauseButton();
        } else setPlayButton();
        setPrevButton();
        setNextButton();
        setTimeSliderEventHandlers();
        toggleShuffle();
    }

    private void setVolumeSlider() {
        volumeSlider.setValue(Jukebox.getVolume() * 100);
        volumeSlider.valueProperty().addListener((observableValue, number, t1) -> {
            Jukebox.setVolume(volumeSlider.getValue() * .01);
            if (Jukebox.getMediaPlayer() != null) {
                Jukebox.getMediaPlayer().setVolume(Jukebox.getVolume());
            }
            setVolumeIndicatorButton();
        });
    }

    private void setTimeSliderEventHandlers() {
        timeSlider.setOnMouseDragged(event -> State.mouseDetected = true);
        timeSlider.setOnMousePressed(event -> State.mouseDetected = true);
        timeSlider.setOnMouseReleased(event -> {
            if (Jukebox.getMediaPlayer() == null) {
                timeSlider.setValue(0);
            } else {
                double seekTime = (timeSlider.getValue() / 100) * Jukebox.getTotalDuration();
                Jukebox.getMediaPlayer().seek(Duration.millis(seekTime));
            }
            State.mouseDetected = false;
        });
    }

    @FXML
    private void onClickPrev() {
        if (Jukebox.getMediaPlayer() != null && Jukebox.isPrev()) {
            Jukebox.prev();
            Song song = Jukebox.getNowPlaying();
            Jukebox.prepare();
            Jukebox.play();
            update(song.getName(), song.getArtistName());
        }
    }

    @FXML
    private void onClickNext() {
        if (Jukebox.getMediaPlayer() != null && Jukebox.isNext()) {
            Jukebox.next();
            Song song = Jukebox.getNowPlaying();
            Jukebox.prepare();
            Jukebox.play();
            update(song.getName(), song.getArtistName());
        }
    }

    @FXML
    private void onClickPlayPause() {
        MediaPlayer mediaPlayer = Jukebox.getMediaPlayer();
        if (mediaPlayer != null) {
            if (mediaPlayer.getStatus() == MediaPlayer.Status.PLAYING) {
                mediaPlayer.pause();
                setPlayButton();
            } else if (mediaPlayer.getStatus() == MediaPlayer.Status.PAUSED) {
                mediaPlayer.play();
                setPauseButton();
            }
        }
    }

    public void setTimeSlider() {
        timeSlider.setValue(0);
        if (Jukebox.getMediaPlayer() != null) {
            Jukebox.getMediaPlayer().currentTimeProperty().addListener((observableValue, duration, t1) -> {
                if (Jukebox.getMediaPlayer() == null) return;
                double totalTime = Jukebox.getMediaPlayer().getTotalDuration().toMillis();
                Jukebox.setTotalDuration(totalTime);
                double currentTime = Jukebox.getMediaPlayer().getCurrentTime().toMillis();
                Jukebox.setPlaybackPos(currentTime);
                double timeSliderValue = (currentTime / totalTime) * timeSlider.getMax();
                if (!State.mouseDetected) timeSlider.setValue(timeSliderValue);
                totalDuration.setText(Tools.timeToString(totalTime));
                elapsed.setText(Tools.timeToString(currentTime));
            });
            Jukebox.getMediaPlayer().setOnEndOfMedia(this::onEndOfSong);
        }
    }

    private void updateNames(String name, String artist) {
        songNameLabel.setText(name);
        artistNameLabel.setText(artist);
    }

    public JFXSlider getTimeSlider() {
        return timeSlider;
    }

    public Label getSongNameLabel() {
        return songNameLabel;
    }

    public Label getArtistNameLabel() {
        return artistNameLabel;
    }

    public Button getPlayPauseButton() {
        return playPauseButton;
    }

    public void setPlayButton() {
        try {
            File file = new File(FilePaths.PLAY_ICON);
            ImageView imageView = new ImageView(file.getAbsolutePath());
            imageView.setFitWidth(15);
            imageView.setFitHeight(20);
            playPauseButton.setGraphic(imageView);
            playPauseButton.getTooltip().setText("Play");
            playPauseButton.getTooltip().setShowDelay(Duration.millis(100));
        } catch (Exception ignore) {
            playPauseButton.setText("Play");
        }
    }

    public void setPauseButton() {
        try {
            File file = new File(FilePaths.PAUSE_ICON);
            ImageView imageView = new ImageView(file.getAbsolutePath());
            imageView.setFitWidth(15);
            imageView.setFitHeight(20);
            playPauseButton.setGraphic(imageView);
            playPauseButton.getTooltip().setText("Pause");
            playPauseButton.getTooltip().setShowDelay(Duration.millis(100));
        } catch (Exception ignore) {
            playPauseButton.setText("Pause");
        }
    }

    private void setNextButton() {
        try {
            File file = new File(FilePaths.NEXT_ICON);
            ImageView imageView = new ImageView(file.getAbsolutePath());
            imageView.setFitWidth(15);
            imageView.setFitHeight(20);
            nextButton.setGraphic(imageView);
            nextButton.getTooltip().setShowDelay(Duration.millis(100));
        } catch (Exception ignore) {
            nextButton.setText("Next");
        }
    }

    private void setPrevButton() {
        try {
            File file = new File(FilePaths.PREV_ICON);
            ImageView imageView = new ImageView(file.getAbsolutePath());
            imageView.setFitWidth(15);
            imageView.setFitHeight(20);
            prevButton.setGraphic(imageView);
            prevButton.getTooltip().setShowDelay(Duration.millis(100));
        } catch (Exception ignore) {
            prevButton.setText("Prev");
        }
    }

    @FXML
    private void toggleMute() {
        if (isMuted) {
            isMuted = false;
            volumeSlider.setValue(volSliderValue);
        } else {
            isMuted = true;
            volSliderValue = volumeSlider.getValue();
            volumeSlider.setValue(0);
        }
    }

    private void setVolumeIndicatorButton() {
        String volumeIndicatorIcon = "";
        if (0 <= volumeSlider.getValue() && volumeSlider.getValue() < 1) {
            volumeIndicatorIcon = FilePaths.VOLUME_INDICATOR_MUTE;
        } else if (1 <= volumeSlider.getValue() && volumeSlider.getValue() < volumeSlider.getMax() * 1 / 3) {
            volumeIndicatorIcon = FilePaths.VOLUME_INDICATOR_LOW;
        } else if (volumeSlider.getMax() * 1 / 3 <= volumeSlider.getValue() && volumeSlider.getValue() < volumeSlider.getMax() * 2 / 3) {
            volumeIndicatorIcon = FilePaths.VOLUME_INDICATOR_MEDIUM;
        } else if (volumeSlider.getMax() * 2 / 3 <= volumeSlider.getValue() && volumeSlider.getValue() <= volumeSlider.getMax()) {
            volumeIndicatorIcon = FilePaths.VOLUME_INDICATOR_HIGH;
        }
        File file = new File(volumeIndicatorIcon);
        ImageView imageView = new ImageView(file.getAbsolutePath());
        imageView.setFitWidth(30);
        imageView.setFitHeight(35);
        volumeIndicator.setGraphic(imageView);
    }

    public void update(String name, String artist) {
        songNameLabel.setText(name);
        artistNameLabel.setText(artist);
        setPauseButton();
        setTimeSlider();
    }

    private void onEndOfSong() {
        if (Jukebox.isNext()) {
            onClickNext();
        } else {
            Jukebox.getMediaPlayer().seek(Duration.millis(0));
            Jukebox.getMediaPlayer().pause();
            setPlayButton();
        }
    }

    @FXML
    private void toggleShuffle() {
        if(State.isShuffleOn()) {
            State.setShuffleOn(false);
            shuffleIndicator.setStyle("-fx-background-color: lightskyblue");
            shuffleIndicator.getTooltip().setText("Shuffle Playlist");
        } else {
            State.setShuffleOn(true);
            shuffleIndicator.setStyle("-fx-background-color: blue");
            shuffleIndicator.getTooltip().setText("Undo Shuffle Playlist");
        }
        Jukebox.setShuffle(State.isShuffleOn());
    }
}
