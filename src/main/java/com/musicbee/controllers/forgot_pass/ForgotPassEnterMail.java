package com.musicbee.controllers.forgot_pass;

import com.musicbee.entities.User;
import com.musicbee.utility.Database;
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

import java.util.Objects;

import static com.musicbee.utility.OTP.sendEmail;

public class ForgotPassEnterMail {
    @FXML
    private TextField emailID;

    @FXML
    private Label noAccount;

    @FXML
    void onSubEmail(ActionEvent event) throws Exception {
        String address = emailID.getText();
        User bou = Database.verifyEmail(address);
        if (bou == null) {
            noAccount.setText("No account found under this email ID.");
            return;
        }

        String val;
        val = sendEmail(address);

        FXMLLoader loader = new FXMLLoader(getClass().getResource(FilePaths.FORGOT_PASS_VERIFY_OTP));
        Parent root = loader.load();

        ForgotPassVerifyOTP forgot = loader.getController();
        forgot.setVal(val);
        forgot.setAddress(address);

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        String css = Objects.requireNonNull(getClass().getResource(FilePaths.STYLESHEET)).toExternalForm();
        scene.getStylesheets().add(css);
        stage.setScene(scene);
        stage.show();
    }
}
