package com.musicbee.controllers;

import com.jfoenix.transitions.hamburger.HamburgerBasicCloseTransition;
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
import javafx.scene.layout.VBox;
import javafx.scene.media.MediaPlayer;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

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
    private Button deletePhoto;

    boolean pfpDeleted = false;

    @FXML
    private VBox controlPanel;

    private File selectedFile;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        menuButton.setText(Database.getCurrentUser().getUsername());
        defaultImage = pfp.getImage();
        pfpDeleted = false;

        initInfo();
        loadSideBar();
        setHamburger();
        loadControlPanel();
    }

    private void setHamburger() {
        HamburgerBasicCloseTransition transition= new HamburgerBasicCloseTransition(myHamburger);
        if(State.getBurgerState()==-1) {
            transition.setRate(-1);
            drawer.close();
        } else {
            transition.setRate(1);
            drawer.open();
        }
        transition.play();
        addHamburgerEventHandler(transition);
    }

    private void addHamburgerEventHandler(HamburgerBasicCloseTransition transition) {
        myHamburger.addEventHandler(MouseEvent.MOUSE_PRESSED,(e)->{
            transition.setRate(transition.getRate()*-1);
            if(transition.getRate()==-1) {
                State.setBurgerState(-1);
            } else {
                State.setBurgerState(1);
            }
            transition.play();
            if(drawer.isOpened() || drawer.isOpening()) {
                drawer.close();
            } else {
                drawer.open();
            }
        });
    }

    private void initInfo() {
        firstName.setText(Database.getCurrentUser().getFirstName());
        lastName.setText(Database.getCurrentUser().getLastName());
        email.setText(Database.getCurrentUser().getEmail());

        if(Database.getCurrentUser().getImage() != null ) {
            pfp.setImage(Database.getCurrentUser().getImage());
            profileIcon.setImage(Database.getCurrentUser().getImage());
        }
    }

    private void loadSideBar() {
        try
        {
            VBox vbox= FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/com/musicbee/musicbee/Sidebar.fxml")));
            drawer.setSidePane(vbox);
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    private void loadControlPanel() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/musicbee/musicbee/ControlPanel.fxml"));
        try {
            VBox vBox = fxmlLoader.load();
            controlPanel.getChildren().clear();
            controlPanel.getChildren().addAll(vBox.getChildren());
        } catch (IOException e) {
            System.out.println(e);
        }
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
        myStage.show();
    }

    @FXML
    private void onCLickSave(ActionEvent event) {
        try {
            updateDatabase();

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

    private void updateDatabase() throws Exception {
        Database.getCurrentUser().setFirstName(firstName.getText());
        Database.getCurrentUser().setLastName(lastName.getText());
        Database.getCurrentUser().setEmail(email.getText());
        Database.updateCurrentUserInfo(Database.getCurrentUser());
        if(selectedFile != null) Database.updateUserPhoto(selectedFile);
        if(pfpDeleted) {
            Database.deleteUserPhoto();
        }
    }

    @FXML
    private void choosePhoto()  {
        if (!selectFile()) return; //could not select the file.
        convertPhoto();
    }

    private void convertPhoto() {
        Image image = null;
        try {
            image = new Image(new FileInputStream(selectedFile));
        } catch (Exception e) {
            System.out.println("Could not load the image");;
        }
        pfp.setImage(image);
    }

    private boolean selectFile() {
        FileChooser fileChooser = new FileChooser();
        Stage stage = new Stage();

        File tmpSelectedFile = fileChooser.showOpenDialog(stage);

        if(tmpSelectedFile != null) {
            selectedFile = tmpSelectedFile;
        }

        return selectedFile != null;
    }

    @FXML
    private void onClickDeletePhoto() {
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
        myStage.show();
    }
}