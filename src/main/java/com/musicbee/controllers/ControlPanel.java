package com.musicbee.controllers;

import com.jfoenix.controls.JFXSlider;
import com.musicbee.entities.Song;
import com.musicbee.utility.Jukebox;
import com.musicbee.utility.State;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
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

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        setTimeSlider();

        volumeSlider.setValue(State.getVolume() * 100);
        System.out.println(State.getVolume());

        if(State.getTotalTime() != 0) {
            timeSlider.setValue(  (State.getLastTimeStamp() / State.getTotalTime()) * 100.0 );
        }

        songName.setText(State.getCurrentSongName());
        artistName.setText(State.getCurrentSongArtist());

        volumeSlider.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {
                State.setVolume(volumeSlider.getValue() * .01);
                if(Jukebox.getMediaPlayer() != null) {
                    Jukebox.getMediaPlayer().setVolume(State.getVolume());
                }
            }
        });

        if(Jukebox.getMediaPlayer() != null && Jukebox.getMediaPlayer().getStatus() == MediaPlayer.Status.PLAYING) {
            setPause();
        }
        else setPlay();
        setPrev();
        setNext();
    }

    @FXML
    private void onClickNext(ActionEvent event) {
        playNext();
    }

    @FXML
    private void onClickPlayPause(ActionEvent event) {
        MediaPlayer mediaPlayer = Jukebox.getMediaPlayer();
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
    private void onClickPrev(ActionEvent event) {
        MediaPlayer player = Jukebox.getMediaPlayer();
        if(player != null && (State.getCurrentSongIndex() - 1 >= 0) ) {
            State.decrementCurrentSongIndex();
            State.setLastSongID(State.getSongsInTable().get(State.getCurrentSongIndex()).getID());
            player.stop();
            player.dispose();
            Song song = State.getSongsInTable().get(State.getCurrentSongIndex());
            Jukebox.prepareJukebox(song);
            Jukebox.play();
            setTimeSlider();
            Jukebox.getMediaPlayer().setVolume(State.getVolume());
            State.setCurrentSongName(State.getSongsInTable().get(State.getCurrentSongIndex()).getName());
            State.setCurrentSongArtist(State.getSongsInTable().get(State.getCurrentSongIndex()).getArtistName());
            updateNames(State.getCurrentSongName(),
                    State.getCurrentSongArtist());
            setPlay();
        }
    }

    private void setTimeSlider() {
        timeSlider.setValue(0);
        if(Jukebox.getMediaPlayer() != null) {
            Jukebox.getMediaPlayer().currentTimeProperty().addListener((observableValue, duration, t1) -> {
                if(Jukebox.getMediaPlayer() == null) return;
                double totalTime = Jukebox.getMediaPlayer().getTotalDuration().toMillis();
                State.setTotalTime(totalTime);
                double currentTime = Jukebox.getMediaPlayer().getCurrentTime().toMillis();
                State.setLastTimeStamp(currentTime);
                double timeSliderValue = (currentTime / totalTime) * 100;
                if(!State.mouseDetected) timeSlider.setValue(timeSliderValue);
            });
            Jukebox.getMediaPlayer().setOnEndOfMedia(this::playNext);
        }
        timeSlider.setOnMouseDragged(event -> {
            State.mouseDetected = true;
        });
        timeSlider.setOnMousePressed(event -> {
            State.mouseDetected = true;
        });
        timeSlider.setOnMouseReleased(event -> {
            if(Jukebox.getMediaPlayer() == null) {
                timeSlider.setValue(0);
            }
            else {
                double seekTime = (timeSlider.getValue() / 100) * State.getTotalTime();
                Jukebox.getMediaPlayer().seek(Duration.millis(seekTime));
            }
            State.mouseDetected = false;
        });
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
        if(Jukebox.getMediaPlayer() != null && (State.getCurrentSongIndex() + 1 < State.getSongsInTable().size()) ) {
            State.incrementCurrentSongIndex();
            State.setLastSongID(State.getSongsInTable().get(State.getCurrentSongIndex()).getID());
            Song song = State.getSongsInTable().get(State.getCurrentSongIndex());
            Jukebox.prepareJukebox(song);
            Jukebox.play();
            State.setCurrentSongName(State.getSongsInTable().get(State.getCurrentSongIndex()).getName());
            State.setCurrentSongArtist(State.getSongsInTable().get(State.getCurrentSongIndex()).getArtistName());
            updateNames(State.getCurrentSongName(),
                    State.getCurrentSongArtist());
            setTimeSlider();
            Jukebox.getMediaPlayer().setVolume(State.getVolume());
            setPause();
        }
    }
    public void setPlay() {
        try {
            File file = new File("src/main/resources/com/musicbee/musicbee/images/play-solid.png");
//        File file = new File("src/main/resources/com/musicbee/musicbee/images/redHeart.png");
            ImageView imageView = new ImageView(file.getAbsolutePath());
            imageView.setFitWidth(15);
            imageView.setFitHeight(20);
            playPause.setGraphic(imageView);
        } catch (Exception ignore) {
            playPause.setText("Play");
        }
    }
    public void setPause() {
        try {
            File file = new File("src/main/resources/com/musicbee/musicbee/images/pause-solid.png");
            ImageView imageView = new ImageView(file.getAbsolutePath());
            imageView.setFitWidth(15);
            imageView.setFitHeight(20);
            playPause.setGraphic(imageView);
        } catch (Exception ignore) {
            playPause.setText("Pause");
        }
    }
    private void setNext() {
        try {
            File file = new File("src/main/resources/com/musicbee/musicbee/images/forward-step-solid.png");
            ImageView imageView = new ImageView(file.getAbsolutePath());
            imageView.setFitWidth(15);
            imageView.setFitHeight(20);
            next.setGraphic(imageView);
        } catch (Exception ignore) {
            next.setText("Next");
        }
    }
    private void setPrev() {
        try {
            File file = new File("src/main/resources/com/musicbee/musicbee/images/backward-step-solid.png");
            ImageView imageView = new ImageView(file.getAbsolutePath());
            imageView.setFitWidth(15);
            imageView.setFitHeight(20);
            prev.setGraphic(imageView);
        } catch (Exception ignore) {
            prev.setText("Prev");
        }
    }
}
