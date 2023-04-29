package com.musicbee.controllers;

import com.musicbee.entities.Song;
import com.musicbee.entities.User;
import com.musicbee.utility.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.util.ArrayList;

public class SignInController {
    @FXML
    private TextField     usernameField;
    @FXML
    private TextField     shownPasswordField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Label         wrong;

    private static void resumePlayback(int playbackSongID, double playbackPosition) {
        Song song = null;
        for (Song s : Database.getAllSongs()) {
            if (s.getID() == playbackSongID) {
                song = s;
                break;
            }
        }
        if (song != null) {
            Jukebox.setSong(song);
            Jukebox.prepare();
            Jukebox.play();
            Jukebox.play();
            Jukebox.getMediaPlayer().setOnReady(() -> {
                double totalTime = Jukebox.getMediaPlayer().getTotalDuration().toMillis();
                Jukebox.setTotalDuration(totalTime);
                Jukebox.getMediaPlayer().seek(Duration.millis(playbackPosition));
                Jukebox.getMediaPlayer().pause();
            });
        }
    }

    @FXML
    protected void onClickSubmit(ActionEvent event) throws Exception {
        try {
            String password;
            if (passwordField.isVisible()) {
                password = passwordField.getText();
            } else {
                password = shownPasswordField.getText();
            }

            String username = usernameField.getText();

            User user = Database.signIn(username, Tools.hashPassword(password));
            if (user != null) user.showInfo();
            else {
                wrong.setText("Wrong Credentials!");
                wrong.setStyle("-fx-text-fill: red;");
                return;
            }
        } catch (Exception e) {
            wrong.setText("Some error occurred while login.");
            wrong.setStyle("-fx-text-fill: red;");
            return;
        }

        ArrayList<Object> playback = Database.loadPlaybackPosition();

        int playbackSongID = (Integer) playback.get(0);
        double playbackPosition = (Double) playback.get(1);

        resumePlayback(playbackSongID, playbackPosition);
        Database.loadAllPlaylists();

        Node callingBtn = (Node) event.getSource();
        Stage stage = (Stage) callingBtn.getScene().getWindow();

        SceneSwitcher sceneSwitcher = new SceneSwitcher(FilePaths.HOME, FilePaths.STYLESHEET, FilePaths.STYLESHEET_3);

        HomeController bl = sceneSwitcher.getController();
        ArrayList<Song> songs = Database.getAllSongs();
        bl.makeObservableList(songs);

        sceneSwitcher.switchNow(stage);
    }

    @FXML
    private void togglePasswordChars(ActionEvent event) {
        Tools.togglePasswordChars(event, passwordField, shownPasswordField);
    }

    @FXML
    void onForgotPass(ActionEvent event) throws IOException {
        Node callingBtn = (Node) event.getSource();
        Stage stage = (Stage) callingBtn.getScene().getWindow();
        SceneSwitcher sceneSwitcher = new SceneSwitcher(FilePaths.FORGOT_PASS_ENTER_MAIL, FilePaths.STYLESHEET);
        sceneSwitcher.switchNow(stage);
    }

    @FXML
    void onSignUpFromLogin(ActionEvent event) throws IOException {
        Node callingBtn = (Node) event.getSource();
        Stage stage = (Stage) callingBtn.getScene().getWindow();
        SceneSwitcher sceneSwitcher = new SceneSwitcher(FilePaths.SIGN_UP, FilePaths.STYLESHEET);
        sceneSwitcher.switchNow(stage);
    }
}
