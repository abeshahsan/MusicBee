package com.musicbee.controllers;

import com.jfoenix.transitions.hamburger.HamburgerBasicCloseTransition;
import com.musicbee.entities.Song;
import com.musicbee.utility.Database;
import com.musicbee.utility.Jukebox;
import com.musicbee.utility.Settings;
import com.musicbee.utility.State;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;

import java.io.*;
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
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;

public class EditProfileController implements Initializable {

    @FXML
    private TextField firstName;
    @FXML
    private TextField lastName;

    @FXML
    private JFXDrawer drawer;

    @FXML
    private TextField email;

    @FXML
    private ImageView pfp;

    private Image defaultImage;
    @FXML
    private ImageView profileIcon;

    @FXML
    private MenuButton menuButton;

    @FXML
    private JFXHamburger myHamburger;

    @FXML
    private TextField searchBar;

    @FXML
    private Button deletePhoto;

    private Slider timeSlider;

    boolean pfpDeleted = false;

    @FXML
    private VBox bottom;

    private File selectedFile;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        if(Database.getCurrentUser() != null) {
            menuButton.setText(Database.getCurrentUser().getUsername());
        }
        else menuButton.setText("------");

        defaultImage = pfp.getImage();

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

        pfpDeleted = false;

        firstName.setText(Database.getCurrentUser().getFirstName());
        lastName.setText(Database.getCurrentUser().getLastName());
        email.setText(Database.getCurrentUser().getEmail());

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
        myStage.setMinWidth(Settings.getMinWidth());
        myStage.setMinHeight(Settings.getMinHeight());
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
        //myStage.setMinWidth(Settings.getMinWidth());
       //myStage.setMinHeight(Settings.getMinHeight());
        myStage.show();
    }

    @FXML
    private void onCLickSave(ActionEvent event) {
        Database.getCurrentUser().setFirstName(firstName.getText());
        Database.getCurrentUser().setLastName(lastName.getText());
        Database.getCurrentUser().setEmail(email.getText());
        try {
            Database.updateCurrentUserInfo(Database.getCurrentUser());
            if(selectedFile != null) Database.updateUserPhoto(selectedFile);
            if(pfpDeleted) {
                Database.deleteUserPhoto();
            }
            Node node = (Node) event.getSource();
            Stage myStage = (Stage) node.getScene().getWindow();

            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/musicbee/musicbee/Profile.fxml"));
            Parent root = fxmlLoader.load();

            Scene scene = new Scene(root);
            String css = Objects.requireNonNull(getClass().getResource("/com/musicbee/musicbee/Stylesheet.css")).toExternalForm();
            scene.getStylesheets().add(css);
            myStage.setScene(scene);
            //myStage.setMinWidth(Settings.getMinWidth());
            //.setMinHeight(Settings.getMinHeight());
            myStage.show();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    @FXML
    private void choosePhoto()  {
        FileChooser fileChooser = new FileChooser();
        Stage stage = new Stage();

        File tmpSelectedFile = fileChooser.showOpenDialog(stage);

        if(tmpSelectedFile != null) {
            selectedFile = tmpSelectedFile;
        }

        if(selectedFile == null) return;

        Image image = null;
        try {
            image = new Image(new FileInputStream(selectedFile));
        } catch (Exception e) {
            System.out.println("Could not load the image");;
        }
        pfp.setImage(image);
    }
    @FXML
    private void onClickDeletePhoto(ActionEvent event) {
        pfpDeleted = true;
        selectedFile = null;
        pfp.setImage(defaultImage);
    }
    @FXML
    private void onClickPwdChange(ActionEvent event) throws IOException {
        Node node = (Node) event.getSource();
        Stage myStage = (Stage) node.getScene().getWindow();

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/musicbee/musicbee/ChangePwd.fxml"));
        Parent root = fxmlLoader.load();

        Scene scene = new Scene(root);
        String css = Objects.requireNonNull(getClass().getResource("/com/musicbee/musicbee/Stylesheet.css")).toExternalForm();
        scene.getStylesheets().add(css);
        myStage.setScene(scene);
        //myStage.setMinWidth(Settings.getMinWidth());
        //.setMinHeight(Settings.getMinHeight());
        myStage.show();
    }
}