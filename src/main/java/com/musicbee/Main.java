package com.musicbee;

import com.musicbee.controllers.HomeController;
import com.musicbee.entities.Song;
import com.musicbee.entities.User;
import com.musicbee.utility.Database;
import com.musicbee.utility.State;
import com.musicbee.utility.Tools;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.*;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Scanner;

public class Main extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/musicbee/musicbee/SignIn.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.getIcons().add( new Image( Objects.requireNonNull(getClass().getResourceAsStream("/com/musicbee/musicbee/images/StageIcon.png")) ) );
        stage.setTitle("Music Bee");
        String css = Objects.requireNonNull(getClass().getResource("/com/musicbee/musicbee/Stylesheet.css")).toExternalForm();
        scene.getStylesheets().add(css);
        stage.setScene(scene);
        stage.setMinWidth(400);
        stage.setMinHeight(300);
        stage.show();
    }

    public static void main(String[] args) {
        try {
            Database.prepareDatabase();
            Database.loadAllSongs();

            launch();

            //before closing the application, save the last state of the user.
            if(Database.getCurrentUser() != null) { //The user actually closed the application without logging out.
                Database.saveLastState();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
