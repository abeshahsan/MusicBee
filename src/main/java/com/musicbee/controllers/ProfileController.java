package com.musicbee.controllers;

import com.jfoenix.transitions.hamburger.HamburgerBasicCloseTransition;
import com.musicbee.entities.Song;
import com.musicbee.utility.Database;
import com.musicbee.utility.Jukebox;
import com.musicbee.utility.State;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.Objects;
import java.util.ResourceBundle;

import com.jfoenix.controls.JFXDrawer;
import com.jfoenix.controls.JFXHamburger;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;
import javafx.util.Duration;

public class ProfileController implements Initializable {

    @FXML
    private Label name;

    @FXML
    private Label dateJoined;

    @FXML
    private JFXDrawer drawer;

    @FXML
    private Label email;

    @FXML
    private ImageView pfp;
    @FXML
    private ImageView profileIcon;

    @FXML
    private MenuButton menuButton;

    @FXML
    private JFXHamburger myHamburger;

    @FXML
    private TextField searchBar;

    @FXML
    private Label username;

    private Slider timeSlider;
    @FXML
    private VBox bottom;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        if(Database.getCurrentUser() != null) {
            menuButton.setText(Database.getCurrentUser().getUsername());
        }
        else menuButton.setText("------");

        try
        {
            VBox vbox= FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/com/musicbee/musicbee/Sidebar.fxml")));
            drawer.setSidePane(vbox);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        HamburgerBasicCloseTransition transition= new HamburgerBasicCloseTransition(myHamburger);
        if(State.getBurgerState()==-1)
        {
            transition.setRate(-1);
            drawer.close();
        }
        else
        {
            transition.setRate(1);
            drawer.open();
        }
        transition.play();
        myHamburger.addEventHandler(MouseEvent.MOUSE_PRESSED,(e)->{
            transition.setRate(transition.getRate()*-1);
            if(transition.getRate()==-1)
            {
                State.setBurgerState(-1);
            }
            else {
                State.setBurgerState(1);
            }
            transition.play();
            if(drawer.isOpened() || drawer.isOpening())
            {
                drawer.close();
            }
            else drawer.open();
        });

        username.setText(Database.getCurrentUser().getUsername());
        name.setText(Database.getCurrentUser().getName());
        email.setText(Database.getCurrentUser().getEmail());
        dateJoined.setText(Database.getCurrentUser().getJoinDate().toString());

        if(Database.getCurrentUser().getImage() != null ) {
            pfp.setImage(Database.getCurrentUser().getImage());
            profileIcon.setImage(Database.getCurrentUser().getImage());
        }

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/musicbee/musicbee/ControlPanel.fxml"));
        try {
            VBox vBox = fxmlLoader.load();
//            ControlPanel bottomController = fxmlLoader.getController();
            bottom.getChildren().clear();
            bottom.getChildren().addAll(vBox.getChildren());
        } catch (IOException e) {
            System.out.println(e);
        }
        HBox hBox = (HBox) bottom.getChildren().get(0);
        AnchorPane anchorPane = (AnchorPane) hBox.getChildren().get(0);
        timeSlider = (Slider) anchorPane.getChildren().get(0);

        if(Jukebox.getMediaPlayer() != null) {
            Jukebox.getMediaPlayer().currentTimeProperty().addListener((observableValue, duration, t1) -> {
                MediaPlayer player = Jukebox.getMediaPlayer();
                if (player != null) {
                    double totalTime = player.getTotalDuration().toMillis();
                    State.setTotalTime(totalTime);
                    double currentTime = player.getCurrentTime().toMillis();
                    double timeSliderValue = (currentTime / totalTime) * 100;
                    if(!State.mouseDetected) timeSlider.setValue(timeSliderValue);
                }
            });
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

    @FXML
    private void onTypedSearchBar() {

    }

    @FXML
    private void onClickProfile(ActionEvent event) throws IOException {
        MenuItem menuItem = (MenuItem) event.getSource();
        Stage myStage = (Stage) menuItem.getParentPopup().getOwnerWindow();

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/musicbee/musicbee/Profile.fxml"));
        Parent root = fxmlLoader.load();
        Scene scene = new Scene(root);
        String css = Objects.requireNonNull(getClass().getResource("/com/musicbee/musicbee/Stylesheet.css")).toExternalForm();
        scene.getStylesheets().add(css);
        myStage.setScene(scene);
        //Settings.updateWidthHeight(myStage);
        myStage.show();
    }

    @FXML
    private void onClickLogOut(ActionEvent event) throws IOException, SQLException {
        MediaPlayer player = Jukebox.getMediaPlayer();

        if(player != null) {
            Jukebox.dispose();
        }

        MenuItem menuItem = (MenuItem) event.getSource();
        Stage myStage = (Stage) menuItem.getParentPopup().getOwnerWindow();

        Database.logOutCurrentUser();

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/musicbee/musicbee/SignIn.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        String css = Objects.requireNonNull(getClass().getResource("/com/musicbee/musicbee/Stylesheet.css")).toExternalForm();
        scene.getStylesheets().add(css);
        myStage.setScene(scene);
        //Settings.updateWidthHeight(myStage);
        myStage.show();
    }

    @FXML
    private void onCLickEdit(ActionEvent event) {
        Node callingBtn=(Node)event.getSource();
        Stage myStage=(Stage)callingBtn.getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/musicbee/musicbee/EditProfile.fxml"));
        Scene scene;
        try {
            scene = new Scene(fxmlLoader.load());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        String css = Objects.requireNonNull(getClass().getResource("/com/musicbee/musicbee/Stylesheet.css")).toExternalForm();
        scene.getStylesheets().add(css);
        myStage.setScene(scene);
        //Settings.updateWidthHeight(myStage);
        myStage.show();
    }
}