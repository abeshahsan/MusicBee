package com.musicbee.controllers;

import com.musicbee.utility.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class MenuButtonController implements Initializable {

    @FXML
    private ImageView  profileIcon;
    @FXML
    private MenuItem identity;
    @FXML
    private Tooltip tooltip;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        if (Database.getCurrentUser().getImage() != null) {
            profileIcon.setImage(Database.getCurrentUser().getImage());
        }

        Tools.clipImageview(profileIcon, 35);
        String s =  Database.getCurrentUser().getName() + "\n" +
                    Database.getCurrentUser().getEmail();
        identity.setText(s);
        tooltip.setText(s);
    }

    @FXML
    private void onClickProfile(ActionEvent event) throws IOException {
        MenuItem menuItem = (MenuItem) event.getSource();
        Stage myStage = (Stage) menuItem.getParentPopup().getOwnerWindow();

        SceneSwitcher sceneSwitcher = new SceneSwitcher(FilePaths.PROFILE, FilePaths.STYLESHEET);
        sceneSwitcher.switchNow(myStage);
    }
    @FXML
    private void onClickLogOut(ActionEvent event) throws IOException, SQLException {
        MediaPlayer player = Jukebox.getMediaPlayer();

        if (player != null) {
            Jukebox.clearMediaPlayer();
        }

        Database.savePlaybackPosition();
        Database.logOutCurrentUser();

        MenuItem menuItem = (MenuItem) event.getSource();
        Stage stage = (Stage) menuItem.getParentPopup().getOwnerWindow();

        SceneSwitcher sceneSwitcher = new SceneSwitcher(FilePaths.SIGN_IN, FilePaths.STYLESHEET);
        sceneSwitcher.switchNow(stage);
    }
}
