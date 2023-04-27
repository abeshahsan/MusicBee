package com.musicbee.utility;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class SceneSwitcher {
    private final Scene      scene;
    private final FXMLLoader fxmlLoader;

    public SceneSwitcher(String sceneScript) throws IOException {
        fxmlLoader = new FXMLLoader(getClass().getResource(sceneScript));
        scene = new Scene(fxmlLoader.load());
    }

    public SceneSwitcher(String sceneScript, String ...stylesheets) throws IOException {
        fxmlLoader = new FXMLLoader(getClass().getResource(sceneScript));
        scene = new Scene(fxmlLoader.load());

        addStylesheets(stylesheets);
    }

    public <T> T getController() {
        return this.fxmlLoader.getController();
    }

    public Scene getScene() {
        return this.scene;
    }

    public void switchNow(Stage stage) {
        stage.setScene(scene);
        stage.show();
    }

    private void addStylesheets(String[] stylesheets) {
        String currentStylesheet = "";
        try {
            for (String sheet : stylesheets) {
                currentStylesheet = sheet;
                String URL = Objects.requireNonNull(getClass().getResource(sheet)).toExternalForm();
                scene.getStylesheets().add(URL);
            }
        } catch (NullPointerException e) {
            System.out.println("Could not add the stylesheet: " + currentStylesheet);
        }
    }
}
