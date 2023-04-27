package com.musicbee.controllers;

import com.musicbee.entities.Song;
import com.musicbee.entities.User;
import com.musicbee.utility.*;
import com.musicbee.utility.Database;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;

import static com.musicbee.utility.OTP.sendEmail;

public class NewUserVerifyOTP {
    @FXML
    private TextField newUserOTP;
    @FXML
    private Label     wrongOTP;
    private User      hello;
    public  String    address, otpVal;

    public void setVal(String add) {
        otpVal = add;
    }

    public void setAddress(String add) {
        address = add;
    }

    public void passUser(User user) {
        this.hello = user;
    }

    @FXML
    void onClickBack(ActionEvent event) {

    }

    @FXML
    void onResendNew(ActionEvent event) throws IOException {
        String val;
        val = sendEmail(address);

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

        SceneSwitcher sceneSwitcher = new SceneSwitcher(FilePaths.PROFILE, FilePaths.STYLESHEET);

        NewUserVerifyOTP controller = sceneSwitcher.getController();
        controller.setVal(val);
        controller.setAddress(address);
        controller.passUser(hello);

        sceneSwitcher.switchNow(stage);

    }

    @FXML
    void onSubmitOTP(ActionEvent event) throws Exception {
        if (!newUserOTP.getText().equals(otpVal)) {
            wrongOTP.setText("Wrong OTP!");
            wrongOTP.setStyle("-fx-text-fill: red");
            return;
        }
        Database.signUp(hello);

        Node callingBtn = (Node) event.getSource();
        Stage stage = (Stage) callingBtn.getScene().getWindow();

        SceneSwitcher sceneSwitcher = new SceneSwitcher(FilePaths.HOME, FilePaths.STYLESHEET);

        HomeController controller = sceneSwitcher.getController();
        ArrayList<Song> songs = Database.getAllSongs();
        controller.makeObservableList(songs);

        sceneSwitcher.switchNow(stage);
    }
}
