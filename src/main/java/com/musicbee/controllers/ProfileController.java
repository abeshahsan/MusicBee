package com.musicbee.controllers;

import com.jfoenix.controls.JFXDrawer;
import com.jfoenix.controls.JFXHamburger;
import com.jfoenix.transitions.hamburger.HamburgerBasicCloseTransition;
import com.musicbee.utility.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.Objects;
import java.util.ResourceBundle;

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
    private Label username;

    @FXML
    private VBox bottom;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        menuButton.setText(Database.getCurrentUser().getUsername());

        initInfo();
        loadSideBar();
        setHamburger();
        loadControlPanel();
    }

    private void initInfo() {
        username.setText(Database.getCurrentUser().getUsername());
        name.setText(Database.getCurrentUser().getName());
        email.setText(Database.getCurrentUser().getEmail());
        dateJoined.setText(Database.getCurrentUser().getJoinDate().toString());

        if(Database.getCurrentUser().getImage() != null ) {
            pfp.setImage(Database.getCurrentUser().getImage());
            profileIcon.setImage(Database.getCurrentUser().getImage());
        }
    }

    private void loadControlPanel() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(FilePaths.CONTROL_PANEL));
        try {
            VBox vBox = fxmlLoader.load();
            bottom.getChildren().clear();
            bottom.getChildren().addAll(vBox.getChildren());
        } catch (IOException e) {
            System.out.println(e.getMessage());
            System.out.println(getClass().getName() + ": " + getClass().getEnclosingMethod());
        }
    }

    private void setHamburger() {
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
        addHamburgerEventHandler(transition);
    }

    private void addHamburgerEventHandler(HamburgerBasicCloseTransition transition) {
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
    }

    private void loadSideBar() {
        try
        {
            VBox vbox= FXMLLoader.load(Objects.requireNonNull(getClass().getResource(FilePaths.SIDE_BAR)));
            drawer.setSidePane(vbox);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    private void onClickProfile(ActionEvent event) throws IOException {
        MenuItem menuItem = (MenuItem) event.getSource();
        Stage stage = (Stage) menuItem.getParentPopup().getOwnerWindow();

        SceneSwitcher sceneSwitcher = new SceneSwitcher(FilePaths.PROFILE, FilePaths.STYLESHEET);
        sceneSwitcher.switchNow(stage);
    }

    @FXML
    private void onClickLogOut(ActionEvent event) throws IOException {
        if(MediaPlayerControl.getMediaPlayer() != null) {
            MediaPlayerControl.clear();
        }

        try {
            Database.savePlaybackPosition();
        } catch (SQLException e) {
            System.out.println("Could not save the user's playback position.");
            System.out.println(getClass().getName() + ": " + getClass().getEnclosingMethod());
        }

        Database.logOutCurrentUser();

        MenuItem menuItem = (MenuItem) event.getSource();
        Stage stage = (Stage) menuItem.getParentPopup().getOwnerWindow();

        SceneSwitcher sceneSwitcher = new SceneSwitcher(FilePaths.SIGN_IN, FilePaths.STYLESHEET);
        sceneSwitcher.switchNow(stage);
    }

    @FXML
    private void onCLickEdit(ActionEvent event) throws IOException {
        Node callingBtn=(Node)event.getSource();
        Stage stage = (Stage)callingBtn.getScene().getWindow();
        SceneSwitcher sceneSwitcher = new SceneSwitcher(FilePaths.EDIT_PROFILE, FilePaths.STYLESHEET);
        sceneSwitcher.switchNow(stage);
    }
}