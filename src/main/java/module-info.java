module com.example.musicbee {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires javafx.media;
    requires com.jfoenix;
    requires java.mail;


    opens com.musicbee.musicbee to javafx.fxml;
    exports com.musicbee.entities;
    opens com.musicbee.entities to javafx.fxml;
    exports com.musicbee.utility;
    opens com.musicbee.utility to javafx.fxml;
    exports com.musicbee.controllers;
    opens com.musicbee.controllers to javafx.fxml;
    exports com.musicbee;
    opens com.musicbee to javafx.fxml;
    exports com.musicbee.controllers.forgotpass;
    opens com.musicbee.controllers.forgotpass to javafx.fxml;
}