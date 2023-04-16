package com.musicbee.controllers.forgotpass;
import com.musicbee.controllers.HomeController;
import com.musicbee.entities.Song;
import com.musicbee.entities.User;
import com.musicbee.utility.Database;
import com.musicbee.utility.Tools;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Objects;

public class ForgotPassChangePwd {
        @FXML
        private Label pwdStrength;
        @FXML
        private TextField shownNewPwd;
        @FXML
        private TextField shownConfirmPwd;
        @FXML
        private PasswordField newPwd;
        @FXML
        private PasswordField confirmPwd;
        @FXML
        Label warning;
        @FXML
        protected void onSaveBtnClicked(ActionEvent event)  {

                String newPwdString, confirmPwdString;

                if(newPwd.isVisible()) newPwdString = newPwd.getText();
                else newPwdString = shownNewPwd.getText();
                if(confirmPwd.isVisible()) confirmPwdString = confirmPwd.getText();
                else confirmPwdString = shownConfirmPwd.getText();

                if (!Objects.equals(confirmPwdString, newPwdString)) {
                        warning.setText("Passwords don't match!");
                        warning.setStyle("-fx-text-fill: red");
                } else {
                        try {
                                User user = new User(Database.getCurrentUser());
                                user.setPassword(Tools.hashPassword(newPwdString));
                                Database.updateCurrentUserInfo(user);
                        } catch (Exception e) {
                                System.out.println(e);
                        }
                        goToHomePage(event, newPwdString);
                }
        }
        @FXML
        protected void toggleNewtPasswordChars(ActionEvent event) {
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
        private void goToHomePage(ActionEvent event, String newPassword) {
                Node callingBtn = (Node) event.getSource();
                Stage myStage = (Stage) callingBtn.getScene().getWindow();
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/musicbee/musicbee/Home.fxml"));

                try {
                        ArrayList<Song> songs = Database.getAllSongs();

                        Parent root = fxmlLoader.load();

                        HomeController bl = fxmlLoader.getController();
                        bl.makeObservableList(songs);

                        Database.getCurrentUser().setPassword(Tools.hashPassword(newPassword));
                        Database.updateCurrentUserInfo(Database.getCurrentUser());

                        String css = Objects.requireNonNull(getClass().getResource("/com/musicbee/musicbee/Stylesheet.css")).toExternalForm();
                        Scene scene = new Scene(root);
                        scene.getStylesheets().add(css);
                        myStage.setScene(scene);
                        myStage.show();
                } catch (Exception e) {
                        //e.printStackTrace();
                        System.out.println(e.getMessage());
                }
        }

        @FXML
        private void typingPwd(KeyEvent keyEvent) {
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
