package com.musicbee;

import com.musicbee.utility.Database;
import com.musicbee.utility.FilePaths;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.util.Objects;

public class Main extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(FilePaths.SIGN_IN));
        Scene scene = new Scene(fxmlLoader.load());
        stage.getIcons().add( new Image( Objects.requireNonNull(getClass().getResourceAsStream("/com/musicbee/musicbee/images/StageIcon.png")) ) );
        stage.setTitle("Music Bee");
        String css = Objects.requireNonNull(getClass().getResource(FilePaths.STYLESHEET)).toExternalForm();
        scene.getStylesheets().add(css);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        try {
            Database.prepareDatabase();
            Database.loadAllSongs();

            launch();

            //If user tries closed the application without logging out, then
            //before closing the application, save the last playback position of the user.
            if(Database.getCurrentUser() != null) {
                try {
                    Database.savePlaybackPosition();
                } catch (SQLException e) {
                    System.out.println("Could not save the user's playback position.");
                    System.out.println(Main.class.getName() + ": " + Main.class.getEnclosingMethod());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
