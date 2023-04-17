package com.musicbee.controllers;

import com.musicbee.entities.Song;
import com.musicbee.entities.User;
import com.musicbee.utility.Database;
import com.musicbee.utility.Jukebox;
import com.musicbee.utility.State;
import com.musicbee.utility.Tools;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

public class SignInController {
    @FXML
    private TextField usernameField;
    @FXML
    private TextField shownPasswordField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Label wrong;

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

        ArrayList<Object> state = Database.getLastState();

        State.setLastSongID((Integer)state.get(0));
        State.setLastTimeStamp((Double)state.get(1));

        resumePlayback();

        Node callingBtn=(Node)event.getSource();
        Stage myStage=(Stage)callingBtn.getScene().getWindow();

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/musicbee/musicbee/Home.fxml"));

        ArrayList<Song> songs = Database.getAllSongs();

        Database.loadAllPlaylists();

        Parent root = loader.load();

        HomeController bl = loader.getController();
        bl.makeObservableList(songs);

        String css = Objects.requireNonNull(getClass().getResource("/com/musicbee/musicbee/Stylesheet.css")).toExternalForm();
        String css2 = Objects.requireNonNull(getClass().getResource("/com/musicbee/musicbee/Stylesheet3.css")).toExternalForm();
        Scene scene = new Scene(root);
        scene.getStylesheets().add(css);
        scene.getStylesheets().add(css2);
        myStage.setScene(scene);
        myStage.show();
    }

    private static void resumePlayback() {
        Song song = null;
        for(Song s : Database.getAllSongs()) {
            if(s.getID() == State.getLastSongID()) {
                song = s;
                break;
            }
        }
        if(song != null) {
            State.setCurrentSongName(song.getName());
            State.setCurrentSongArtist(song.getArtistName());
            Jukebox.prepareJukebox(song);
            Jukebox.play();
            Jukebox.getMediaPlayer().setVolume(State.getVolume());
            Jukebox.getMediaPlayer().setOnReady(()-> {
                double totalTime = Jukebox.getMediaPlayer().getTotalDuration().toMillis();
                State.setTotalTime(totalTime);
                Jukebox.getMediaPlayer().seek(Duration.millis(State.getLastTimeStamp()));
                Jukebox.getMediaPlayer().pause();
            });
        }
    }

    @FXML
    private void togglePasswordChars(ActionEvent event) {
        try {
            ToggleButton toggleButton = (ToggleButton) event.getSource();
            if (passwordField.isVisible()) {
                passwordField.setVisible(false);
                shownPasswordField.setVisible(true);
                shownPasswordField.setText(passwordField.getText());
                toggleButton.setText("Hide");
            } else {
                shownPasswordField.setVisible(false);
                passwordField.setVisible(true);
                passwordField.setText(shownPasswordField.getText());
                toggleButton.setText("Show");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    void onForgotPass(ActionEvent event) throws IOException {
        Node callingBtn = (Node) event.getSource();
        Stage myStage = (Stage) callingBtn.getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/musicbee/musicbee/ForgotPassEnterMail.fxml"));
        Scene scene= new Scene(fxmlLoader.load());
        String css = Objects.requireNonNull(getClass().getResource("/com/musicbee/musicbee/Stylesheet.css")).toExternalForm();
        scene.getStylesheets().add(css);
        myStage.setScene(scene);
        myStage.show();
    }

    @FXML
    void onSignUpFromLogin(ActionEvent event) throws IOException {
        Node callingBtn = (Node) event.getSource();
        Stage myStage = (Stage) callingBtn.getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/musicbee/musicbee/SignUp.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        String css = Objects.requireNonNull(getClass().getResource("/com/musicbee/musicbee/Stylesheet.css")).toExternalForm();
        scene.getStylesheets().add(css);
        myStage.setScene(scene);
        myStage.show();
    }
}

// final String val1="";

// create a tile pane
//    Node callingBtn = (Node) event.getSource();
//    Stage myStage = (Stage) callingBtn.getScene().getWindow();
//    TilePane r = new TilePane();
//
//    // create a text input dialog
//    TextInputDialog td = new TextInputDialog("enter any text");
//
//    // setHeaderText
//        td.setHeaderText("enter your name");
//
//    // create a button
//    Button d = new Button("click");
//
//    // create a event handler
//    EventHandler<ActionEvent> event1 = new EventHandler<ActionEvent>() {
//        public void handle(ActionEvent e)
//        {
//            // show the text input dialog
//            td.show();
//            TextField inputField = td.getEditor();
//            String val =inputField.getText();
//            System.out.println(val);
//        }
//    };
//
//    // set on action of event
//        d.setOnAction(event1);
//
//    // add button and label
//        r.getChildren().add(d);
//
//    // create a scene
//    Scene sc = new Scene(r, 500, 300);
//
//    // set the scene
//        myStage.setScene(sc);
