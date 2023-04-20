package com.musicbee.controllers.forgotpass;

import com.musicbee.controllers.ChangePwdController;
import com.musicbee.controllers.HomeController;
import com.musicbee.entities.Song;
import com.musicbee.entities.User;
import com.musicbee.utility.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.Objects;

public class ForgotPassChangePwd {
    @FXML
    Label warning;
    @FXML
    private Label         pwdStrength;
    @FXML
    private TextField     shownNewPwd;
    @FXML
    private TextField     shownConfirmPwd;
    @FXML
    private PasswordField newPwd;
    @FXML
    private PasswordField confirmPwd;

    @FXML
    protected void onSaveBtnClicked(ActionEvent event) {

        String newPwdString, confirmPwdString;

        if (newPwd.isVisible()) newPwdString = newPwd.getText();
        else newPwdString = shownNewPwd.getText();
        if (confirmPwd.isVisible()) confirmPwdString = confirmPwd.getText();
        else confirmPwdString = shownConfirmPwd.getText();

        if (!Objects.equals(confirmPwdString, newPwdString)) {
            warning.setText("Passwords don't match!");
            warning.setStyle("-fx-text-fill: red");
        } else {
            try {
                User user = new User(Database.getCurrentUser());
                user.setPassword(Tools.hashPassword(newPwdString));
                Database.updateCurrentUserInfo(user);
            } catch (Exception e) {
                e.printStackTrace();
            }
            goToHomePage(event, newPwdString);
        }
    }

    @FXML
    protected void toggleNewtPasswordChars(ActionEvent event) {
        ChangePwdController.togglePasswordChars(event, newPwd, shownNewPwd);
    }

    @FXML
    protected void toggleConfirmPasswordChars(ActionEvent event) {
        ChangePwdController.togglePasswordChars(event, confirmPwd, shownConfirmPwd);
    }

    private void goToHomePage(ActionEvent event, String newPassword) {
        try {
            ArrayList<Song> songs = Database.getAllSongs();


            Database.getCurrentUser().setPassword(Tools.hashPassword(newPassword));
            Database.updateCurrentUserInfo(Database.getCurrentUser());

            ArrayList<Object> state = Database.loadPlaybackPosition();

            State.setLastSongID((Integer) state.get(0));
            State.setPlaybackPos((Double) state.get(1));

            resumePlayback();
            Database.loadAllPlaylists();

            Node callingBtn = (Node) event.getSource();
            Stage stage = (Stage) callingBtn.getScene().getWindow();

            SceneSwitcher sceneSwitcher = new SceneSwitcher(FilePaths.HOME, FilePaths.STYLESHEET);
            HomeController bl = sceneSwitcher.getController();
            bl.makeObservableList(songs);
            sceneSwitcher.switchNow(stage);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @FXML
    private void typingPwd(KeyEvent keyEvent) {
        String pass;

        if (newPwd.isVisible()) pass = newPwd.getText();
        else pass = shownNewPwd.getText();

        if (pass.isEmpty()) {
            pwdStrength.setText("");
        }
        if (Tools.calcStrength(pass) == 1) {
            pwdStrength.setText("Weak");
        } else if (Tools.calcStrength(pass) == 2) {
            pwdStrength.setText("Medium");
        }
        if (Tools.calcStrength(pass) == 3) {
            pwdStrength.setText("Strong");
        }
    }

    private void resumePlayback() {
        Song song = null;
        for (Song s : Database.getAllSongs()) {
            if (s.getID() == State.getLastSongID()) {
                song = s;
                break;
            }
        }
        if (song != null) {
            State.setCurrentSongName(song.getName());
            State.setCurrentSongArtist(song.getArtistName());
            MediaPlayerControl.prepareJukebox(song);
            MediaPlayerControl.play();
            MediaPlayerControl.getMediaPlayer().setVolume(State.getVolume());
            MediaPlayerControl.getMediaPlayer().setOnReady(() -> {
                double totalTime = MediaPlayerControl.getMediaPlayer().getTotalDuration().toMillis();
                State.setTotalDuration(totalTime);
                MediaPlayerControl.getMediaPlayer().seek(Duration.millis(State.getPlaybackPos()));
                MediaPlayerControl.getMediaPlayer().pause();
            });
        }
    }
}
