package com.musicbee.controllers;

import com.musicbee.entities.User;
import com.musicbee.utility.Database;
import com.musicbee.utility.FilePaths;
import com.musicbee.utility.SceneSwitcher;
import com.musicbee.utility.Tools;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class ChangePwdController {

    @FXML
    Label warning;
    @FXML
    private Label         pwdStrength;
    @FXML
    private TextField     shownNewPwd;
    @FXML
    private TextField     shownCurrPwd;
    @FXML
    private TextField     shownConfirmPwd;
    @FXML
    private PasswordField newPwd;
    @FXML
    private PasswordField currPwd;
    @FXML
    private PasswordField confirmPwd;

    public static void togglePasswordChars(ActionEvent event, PasswordField currPwd, TextField shownCurrPwd) {
        try {
            ToggleButton toggleButton = (ToggleButton) event.getSource();
            if (currPwd.isVisible()) {
                currPwd.setVisible(false);
                shownCurrPwd.setVisible(true);
                shownCurrPwd.setText(currPwd.getText());
                toggleButton.setText("Hide");
            } else {
                shownCurrPwd.setVisible(false);
                currPwd.setVisible(true);
                currPwd.setText(shownCurrPwd.getText());
                toggleButton.setText("Show");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    protected void onSaveBtnClicked() {

        String currentPwdString, newPwdString, confirmPwdString;

        if (currPwd.isVisible()) currentPwdString = currPwd.getText();
        else currentPwdString = shownCurrPwd.getText();
        if (newPwd.isVisible()) newPwdString = newPwd.getText();
        else newPwdString = shownNewPwd.getText();
        if (confirmPwd.isVisible()) confirmPwdString = confirmPwd.getText();
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
                System.out.println(e.getMessage());
                System.out.println(getClass().getName() + ": " + getClass().getEnclosingMethod());
            }
            warning.setText("Password is updated.");
            warning.setStyle("-fx-text-fill: green");
        }
    }

    @FXML
    protected void onBackButton(ActionEvent event) throws IOException {
        Node callButton = (Node) event.getSource();
        Stage stage = (Stage) callButton.getScene().getWindow();

        SceneSwitcher sceneSwitcher = new SceneSwitcher(FilePaths.EDIT_PROFILE, FilePaths.STYLESHEET);
        sceneSwitcher.switchNow(stage);
    }

    @FXML
    protected void toggleCurrentPasswordChars(ActionEvent event) {
        togglePasswordChars(event, currPwd, shownCurrPwd);
    }

    @FXML
    protected void toggleNewPasswordChars(ActionEvent event) {
        togglePasswordChars(event, newPwd, shownNewPwd);
    }

    @FXML
    protected void toggleConfirmPasswordChars(ActionEvent event) {
        togglePasswordChars(event, confirmPwd, shownConfirmPwd);
    }

    @FXML
    private void typingPwd() {
        String pass;

        if (newPwd.isVisible()) pass = newPwd.getText();
        else pass = shownNewPwd.getText();

        if (pass.isEmpty()) {
            pwdStrength.setText("");
        }
        if (Tools.calcStrength(pass) == 1) {
            pwdStrength.setText("Weak");
            pwdStrength.setStyle("-fx-text-fill: red");
        } else if (Tools.calcStrength(pass) == 2) {
            pwdStrength.setText("Medium");
            pwdStrength.setStyle("-fx-text-fill: #ff9900");
        }
        if (Tools.calcStrength(pass) == 3) {
            pwdStrength.setText("Strong");
            pwdStrength.setStyle("-fx-text-fill: #07f307");
        }
    }
}
