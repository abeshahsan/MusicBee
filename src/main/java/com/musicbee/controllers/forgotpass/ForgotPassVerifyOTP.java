package com.musicbee.controllers.forgotpass;

import com.musicbee.utility.FilePaths;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

import static com.musicbee.utility.OTP.sendEmail;

//import static com.musicbee.musicbee.utility.OTP.sendEmail;

public class ForgotPassVerifyOTP {

    @FXML
    private TextField otp;

    @FXML
    private Label wrongOTPforgot;
    public String address, otpVal;

//    public ForgotPassVerifyOTP(String prevotp, String add){
//        this.address=add;
//        this.otpVal=prevotp;
//    }
    public void setVal(String add){
       otpVal = add;
        //this.otpVal=prevotp;
    }
    public void setAddress(String add){
        address = add;
        //this.otpVal=prevotp;
    }

    @FXML
    void onBack100(ActionEvent event) throws IOException {
        Node callingBtn = (Node) event.getSource();
        Stage myStage = (Stage) callingBtn.getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(FilePaths.SIGN_IN));
        Scene scene = new Scene(fxmlLoader.load());
        String css = Objects.requireNonNull(getClass().getResource(FilePaths.STYLESHEET)).toExternalForm();
        scene.getStylesheets().add(css);
        myStage.setScene(scene);
        myStage.show();

    }

    @FXML
    void onClickSubmit(ActionEvent event) throws IOException {
        String val=otp.getText();
//        System.out.println(otpVal);
        if(!val.equals(otpVal)) wrongOTPforgot.setText("Wrong OTP!");
        else {
            Node callingBtn = (Node) event.getSource();
            Stage myStage = (Stage) callingBtn.getScene().getWindow();
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(FilePaths.FORGOT_PASS_CHANGE_PWD));
            Scene scene = new Scene(fxmlLoader.load());
            //myStage.setMinWidth(Settings.getMinWidth());
            //myStage.setMinHeight(Settings.getMinHeight());
            String css = Objects.requireNonNull(getClass().getResource(FilePaths.STYLESHEET)).toExternalForm();
            scene.getStylesheets().add(css);
            myStage.setScene(scene);
            myStage.show();

        }
    }

    @FXML
    void onResendforgot(ActionEvent event) throws IOException {
        String val=sendEmail(address);
        System.out.println(val);
        FXMLLoader loader = new FXMLLoader(getClass().getResource(FilePaths.FORGOT_PASS_VERIFY_OTP));
        Parent root = loader.load();
        ForgotPassVerifyOTP forgot = loader.getController();
        forgot.setVal(val);
        forgot.setAddress(address);
        //root = FXMLLoader.load(getClass().getResource("Scene2.fxml"));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        String css = Objects.requireNonNull(getClass().getResource(FilePaths.STYLESHEET)).toExternalForm();
        scene.getStylesheets().add(css);
        stage.setScene(scene);
        stage.show();

    }

}
