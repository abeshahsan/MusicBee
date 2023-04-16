package com.musicbee.controllers;

import com.musicbee.entities.User;
import com.musicbee.utility.Database;
import com.musicbee.utility.Tools;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class ChangePwdController {

    @FXML
    private Label pwdStrength;
    @FXML
    private TextField shownNewPwd;
    @FXML
    private TextField shownCurrPwd;
    @FXML
    private TextField shownConfirmPwd;
    @FXML
    private PasswordField newPwd;
    @FXML
    private PasswordField currPwd;
    @FXML
    private PasswordField confirmPwd;

    @FXML
    Label warning;

    @FXML
    protected void onSaveBtnClicked()  {

        String currentPwdString, newPwdString, confirmPwdString;

        if(currPwd.isVisible()) currentPwdString = currPwd.getText();
        else currentPwdString = shownCurrPwd.getText();
        if(newPwd.isVisible()) newPwdString = newPwd.getText();
        else newPwdString = shownNewPwd.getText();
        if(confirmPwd.isVisible()) confirmPwdString = confirmPwd.getText();
        else confirmPwdString = shownConfirmPwd.getText();

        if (!Objects.equals(confirmPwdString, newPwdString)) {
            warning.setText("Passwords don't match!");
            warning.setStyle("-fx-text-fill: red");
        } else if (Tools.hashPassword(currentPwdString) != (Database.getCurrentUser().getPassword())) {
            warning.setText("Incorrect current password!");
            warning.setStyle("-fx-text-fill: red");
        } else {
            try {
                User user = new User(Database.getCurrentUser());
                user.setPassword(Tools.hashPassword(newPwdString));
                Database.updateCurrentUserInfo(user);
            } catch (Exception e) {
                System.out.println(e);
            }
            warning.setText("Password is updated.");
            warning.setStyle("-fx-text-fill: green");
        }
    }
    @FXML
    protected void onBackButton(ActionEvent event) throws IOException {
        Node callButton=(Node)event.getSource();
        Stage myStage= (Stage) callButton.getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/musicbee/musicbee/EditProfile.fxml"));
        Scene scene = new Scene(fxmlLoader.load());

        String css = Objects.requireNonNull(getClass().getResource("/com/musicbee/musicbee/Stylesheet.css")).toExternalForm();
        scene.getStylesheets().add(css);
        myStage.setScene(scene);
        //myStage.setMinWidth(Settings.getMinWidth());
        //myStage.setMinHeight(Settings.getMinHeight());
        myStage.show();
    }

    @FXML
    protected void toggleCurrentPasswordChars(ActionEvent event) {
        try{
            ToggleButton toggleButton = (ToggleButton) event.getSource();
            if(currPwd.isVisible()) {
                currPwd.setVisible(false);
                shownCurrPwd.setVisible(true);
                shownCurrPwd.setText(currPwd.getText());
                toggleButton.setText("Hide");
            }
            else {
                shownCurrPwd.setVisible(false);
                currPwd.setVisible(true);
                currPwd.setText(shownCurrPwd.getText());
                toggleButton.setText("Show");
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
    @FXML
    protected void toggleNewPasswordChars(ActionEvent event) {
        try {
            ToggleButton toggleButton = (ToggleButton) event.getSource();
            if(newPwd.isVisible()) {
                newPwd.setVisible(false);
                shownNewPwd.setVisible(true);
                shownNewPwd.setText(newPwd.getText());
                toggleButton.setText("Hide");
            }
            else {
                shownNewPwd.setVisible(false);
                newPwd.setVisible(true);
                newPwd.setText(shownNewPwd.getText());
                toggleButton.setText("Show");
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
    @FXML
    protected void toggleConfirmPasswordChars(ActionEvent event) {
        try {
            ToggleButton toggleButton = (ToggleButton) event.getSource();
            if(confirmPwd.isVisible()) {
                confirmPwd.setVisible(false);
                shownConfirmPwd.setVisible(true);
                shownConfirmPwd.setText(confirmPwd.getText());
                toggleButton.setText("Hide");
            }
            else {
                shownConfirmPwd.setVisible(false);
                confirmPwd.setVisible(true);
                confirmPwd.setText(shownConfirmPwd.getText());
                toggleButton.setText("Show");
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
    @FXML
    private void typingPwd(KeyEvent keyEvent) {
        //
        // pwdStrength.setText("typing");
        String pass;

        if(newPwd.isVisible()) pass = newPwd.getText();
        else pass = shownNewPwd.getText();


        if(pass.isEmpty()) {
            pwdStrength.setText("");
        }
        if(Tools.calcStrength(pass)==1) {
            pwdStrength.setText("Weak");
        }
        else if(Tools.calcStrength(pass)==2) {
            pwdStrength.setText("Medium");
        }
        if(Tools.calcStrength(pass)==3) {
            pwdStrength.setText("Strong");
        }
    }
}
