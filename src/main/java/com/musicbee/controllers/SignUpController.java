package com.musicbee.controllers;

import com.musicbee.entities.User;
import com.musicbee.utility.FilePaths;
import com.musicbee.utility.Tools;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

import static com.musicbee.utility.OTP.sendEmail;
import static com.musicbee.utility.Database.checkForEmail;
import static com.musicbee.utility.Database.checkForUserName;

public class SignUpController {

    @FXML
    private Label pwdStrength;
    @FXML
    private TextField username;
    @FXML
    private TextField shownPassword;
    @FXML
    private PasswordField password;
    @FXML
    private TextField firstName;
    @FXML
    private TextField lastName;
    @FXML
    private TextField emailID;
    public User user;
    @FXML
    private TextField ShownConfirmPwd;
    @FXML
    private PasswordField ConfirmPwd;
    @FXML
    private Label warning;

    @FXML
    protected void OnClickSubmit(ActionEvent event) throws IOException {
        User user;
        try {

            String pass, conPass;

            if (password.isVisible()) {
                pass = password.getText();
            } else {
                pass = shownPassword.getText();
            }

            if (ConfirmPwd.isVisible()) {
                conPass = ConfirmPwd.getText();
            } else {
                conPass = ShownConfirmPwd.getText();
            }

            if (username.getText().equals("")) {
                warning.setText("You must enter username.");
                warning.setStyle("-fx-text-fill: red;");
                return;
            }
            else if(checkForUserName(username.getText()))
            {
                warning.setText("Username is taken!");
                warning.setStyle("-fx-text-fill: red;");
                return;
            }
            if (pass.equals("")) {
                warning.setText("You must enter a password.");
                warning.setStyle("-fx-text-fill: red;");
                return;
            }

            if (emailID.getText().equals("")) {
                warning.setText("You must enter your email.");
                warning.setStyle("-fx-text-fill: red;");
                return;
            }
            else if(checkForEmail(emailID.getText(), this.username.getText()))
            {
                warning.setText("This account already exists! Want to login?");
                warning.setStyle("-fx-text-fill: red;");
                return;
            }
            if(!pass.equals(conPass)) {
                warning.setText("Passwords don't match!");
                warning.setStyle("-fx-text-fill: red;");
                return;
            }
            user = new User(username.getText(), Tools.hashPassword(pass));
            user.setFirstName(firstName.getText());
            user.setLastName(lastName.getText());
            user.setEmail(emailID.getText());

        } catch (Exception e) {
            warning.setText("Some error occurred while sign-up.");
            warning.setStyle("-fx-text-fill: red;");
            System.out.println(e);
            return;
        }
        String val = sendEmail(emailID.getText());
        System.out.println(val);
        FXMLLoader loader = new FXMLLoader(getClass().getResource(FilePaths.NEW_USER_VERIFY_OTP));
        Parent root = loader.load();
        NewUserVerifyOTP forgot = loader.getController();
        forgot.setVal(val);
        forgot.setAddress(emailID.getText());
        forgot.passUser(user);
        //root = FXMLLoader.load(getClass().getResource("Scene2.fxml"));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        String css = Objects.requireNonNull(getClass().getResource(FilePaths.STYLESHEET)).toExternalForm();
        scene.getStylesheets().add(css);
        stage.setScene(scene);
        stage.show();

    }
    @FXML
    protected void togglePasswordChars(ActionEvent event) {
        try {
            ToggleButton toggleButton = (ToggleButton) event.getSource();
            if(password.isVisible()) {
                password.setVisible(false);
                shownPassword.setVisible(true);
                shownPassword.setText(password.getText());
                toggleButton.setText("Hide");
            }
            else {
                shownPassword.setVisible(false);
                password.setVisible(true);
                password.setText(shownPassword.getText());
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
            if(ConfirmPwd.isVisible()) {
                ConfirmPwd.setVisible(false);
                ShownConfirmPwd.setVisible(true);
                ShownConfirmPwd.setText(ConfirmPwd.getText());
                toggleButton.setText("Hide");
            }
            else {
                ShownConfirmPwd.setVisible(false);
                ConfirmPwd.setVisible(true);
                ConfirmPwd.setText(ShownConfirmPwd.getText());
                toggleButton.setText("Show");
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
    @FXML
    void onLoginFromSignup(ActionEvent event) throws IOException {
        Node callingBtn=(Node)event.getSource();
        Stage myStage=(Stage)callingBtn.getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(FilePaths.SIGN_IN));
        Scene scene = new Scene(fxmlLoader.load());
        String css = Objects.requireNonNull(getClass().getResource(FilePaths.STYLESHEET)).toExternalForm();
        scene.getStylesheets().add(css);
        myStage.setScene(scene);
        myStage.show();

    }
    @FXML
    private void typingPwd(KeyEvent keyEvent) {
        String pass;

        if(password.isVisible()) pass = password.getText();
        else pass = shownPassword.getText();

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
