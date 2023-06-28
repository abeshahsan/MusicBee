package com.musicbee.controllers.forgot_pass;

import com.musicbee.utility.FilePaths;
import com.musicbee.utility.SceneSwitcher;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

import static com.musicbee.utility.OTP.sendEmail;

public class ForgotPassVerifyOTP {

    @FXML
    private TextField otp;

    @FXML
    private Label wrongOTP;
    public String address, otpVal;

    public void setVal(String add){
       otpVal = add;
    }
    public void setAddress(String add){
        address = add;
    }

    @FXML
    void onBack100(ActionEvent event) throws IOException {
        Node callingBtn = (Node) event.getSource();
        Stage stage = (Stage) callingBtn.getScene().getWindow();

        SceneSwitcher sceneSwitcher = new SceneSwitcher(FilePaths.SIGN_IN, FilePaths.STYLESHEET);
        sceneSwitcher.switchNow(stage);
    }

    @FXML
    void onClickSubmit(ActionEvent event) throws IOException {
        String val=otp.getText();
        if(!val.equals(otpVal)) wrongOTP.setText("Wrong OTP!");
        else {
            Node callingBtn = (Node) event.getSource();
            Stage stage = (Stage) callingBtn.getScene().getWindow();

            SceneSwitcher sceneSwitcher = new SceneSwitcher(FilePaths.FORGOT_PASS_CHANGE_PWD, FilePaths.STYLESHEET);
            sceneSwitcher.switchNow(stage);
        }
    }

    @FXML
    void onclickResend(ActionEvent event) {
        otpVal = sendEmail(address);
        otp.setText("");
        wrongOTP.setText("");
    }
}
