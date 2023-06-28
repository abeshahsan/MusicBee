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
import javafx.scene.control.Button;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.Objects;
import java.util.ResourceBundle;

public class EditProfileController implements Initializable {

    boolean pfpDeleted = false;
    @FXML
    private BorderPane borderPane;
    @FXML
    private TextField  firstName;
    @FXML
    private TextField  lastName;
    @FXML
    private JFXDrawer  drawer;
    @FXML
    private TextField  email;
    @FXML
    private ImageView  pfp;
    private Image      defaultImage;

    @FXML
    private MenuButton   menuButton;
    @FXML
    private JFXHamburger myHamburger;
    @FXML
    private Button       deletePhoto;

    private File selectedFile;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        defaultImage = pfp.getImage();
        pfpDeleted = false;

        Tools.clipImageview(pfp, 140);

        loadMenuButton();
        initInfo();
        loadSideBar();
        setHamburger();
        loadControlPanel();
    }

    private void setHamburger() {
        HamburgerBasicCloseTransition transition = new HamburgerBasicCloseTransition(myHamburger);
        if (State.getBurgerState() == -1) {
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
        myHamburger.addEventHandler(MouseEvent.MOUSE_PRESSED, (e) -> {
            transition.setRate(transition.getRate() * -1);
            if (transition.getRate() == -1) {
                State.setBurgerState(-1);
            } else {
                State.setBurgerState(1);
            }
            transition.play();
            if (drawer.isOpened() || drawer.isOpening()) {
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

        if (Database.getCurrentUser().getImage() != null) {
            pfp.setImage(Database.getCurrentUser().getImage());
        }
    }

    private void loadSideBar() {
        try {
            VBox vbox = FXMLLoader.load(Objects.requireNonNull(getClass().getResource(FilePaths.SIDE_BAR)));
            drawer.setSidePane(vbox);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println(getClass().getName() + ": " + getClass().getEnclosingMethod());
        }
    }

    private void loadControlPanel() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(FilePaths.CONTROL_PANEL));
        try {
            VBox vBox = fxmlLoader.load();
            borderPane.setBottom(vBox);
        } catch (IOException e) {
            System.out.println(e.getMessage());
            System.out.println(getClass().getName() + ": " + getClass().getEnclosingMethod());
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
    private void onClickLogOut(ActionEvent event) throws IOException, SQLException {
        if (Jukebox.getMediaPlayer() != null) {
            Jukebox.clearMediaPlayer();
        }

        Database.savePlaybackPosition();
        Database.logOutCurrentUser();

        MenuItem menuItem = (MenuItem) event.getSource();
        Stage stage = (Stage) menuItem.getParentPopup().getOwnerWindow();

        SceneSwitcher sceneSwitcher = new SceneSwitcher(FilePaths.SIGN_IN, FilePaths.STYLESHEET);
        sceneSwitcher.switchNow(stage);
    }

    @FXML
    private void onCLickSave(ActionEvent event) {
        try {
            updateDatabase();

            Node node = (Node) event.getSource();
            Stage stage = (Stage) node.getScene().getWindow();

            SceneSwitcher sceneSwitcher = new SceneSwitcher(FilePaths.PROFILE, FilePaths.STYLESHEET);
            sceneSwitcher.switchNow(stage);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println(getClass().getName() + ": " + getClass().getEnclosingMethod());
        }
    }

    private void updateDatabase() throws Exception {
        Database.getCurrentUser().setFirstName(firstName.getText());
        Database.getCurrentUser().setLastName(lastName.getText());
        Database.getCurrentUser().setEmail(email.getText());
        Database.updateCurrentUserInfo(Database.getCurrentUser());
        if (selectedFile != null) Database.updateUserPhoto(selectedFile);
        if (pfpDeleted) {
            Database.deleteUserPhoto();
        }
    }

    @FXML
    private void choosePhoto() {
        if (!selectFile()) return; //could not select the file.
        convertPhoto();
        pfpDeleted = false;
    }

    private void convertPhoto() {
        Image image = null;
        try {
            image = new Image(new FileInputStream(selectedFile));
        } catch (Exception e) {
            System.out.println("Could not load the image");
        }
        pfp.setImage(image);
    }

    private boolean selectFile() {
        FileChooser fileChooser = new FileChooser();
        Stage stage = new Stage();

        File tmpSelectedFile = fileChooser.showOpenDialog(stage);

        if (tmpSelectedFile != null) {
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
        Stage stage = (Stage) node.getScene().getWindow();

        SceneSwitcher sceneSwitcher = new SceneSwitcher(FilePaths.CHANGE_PWD, FilePaths.STYLESHEET);
        sceneSwitcher.switchNow(stage);
    }

    private void loadMenuButton() {
        try {
            MenuButton menuButton1 = FXMLLoader.load(Objects.requireNonNull(getClass().getResource(FilePaths.MENU_BUTTON)));
            menuButton.getItems().clear();
            menuButton.getItems().addAll(menuButton1.getItems());
            menuButton.setGraphic(menuButton1.getGraphic());
            menuButton.setTooltip(menuButton1.getTooltip());
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println(getClass().getName() + ": " + getClass().getEnclosingMethod());
        }
    }
}