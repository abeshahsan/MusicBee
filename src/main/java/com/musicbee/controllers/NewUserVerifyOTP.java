package com.musicbee.controllers;

import com.musicbee.entities.Song;
import com.musicbee.entities.User;
import com.musicbee.entities.*;
import com.musicbee.utility.*;
import com.musicbee.utility.Database;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

import static com.musicbee.utility.OTP.sendEmail;

public class NewUserVerifyOTP {
        @FXML
        private TextField newUserotp;
        @FXML
        private Label wrongOTPnew;
        private User hello;
        public String address, otpVal;

        public void setVal(String add){
                otpVal = add;
        }
        public void setAddress(String add){
                address = add;
        }

        public void passUser(User user) {
                this.hello = user;
        }

        @FXML
        void onBacknewuser(ActionEvent event) {

        }


        @FXML
        void onResendNew(ActionEvent event) throws IOException {
                String val=sendEmail(address);
                System.out.println(val);
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/musicbee/musicbee/NewUserVerifyOTP.fxml"));
                Parent root = loader.load();
                NewUserVerifyOTP forgot = loader.getController();
                forgot.setVal(val);
                forgot.setAddress(address);
                forgot.passUser(hello);
                //root = FXMLLoader.load(getClass().getResource("Scene2.fxml"));
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                Scene scene = new Scene(root);
                String css = Objects.requireNonNull(getClass().getResource("/com/musicbee/musicbee/Stylesheet.css")).toExternalForm();
                scene.getStylesheets().add(css);
                stage.setScene(scene);
                stage.show();
        }
        @FXML
        void onSubmitOTP(ActionEvent event) throws Exception {

        if(!newUserotp.getText().equals(otpVal)){ wrongOTPnew.setText("Wrong OTP!"); return;     }
        Database.signUp(hello);
                Node callingBtn=(Node)event.getSource();
                Stage myStage=(Stage)callingBtn.getScene().getWindow();

                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/musicbee/musicbee/Home.fxml"));

                ArrayList<Song> songs = Database.getAllSongs();
                Parent root = loader.load();

                HomeController bl = loader.getController();
                bl.makeObservableList(songs);
                String css = Objects.requireNonNull(getClass().getResource("/com/musicbee/musicbee/Stylesheet.css")).toExternalForm();
                Scene scene = new Scene(root);
                scene.getStylesheets().add(css);
                myStage.setScene(scene);
                myStage.show();
        }
}
