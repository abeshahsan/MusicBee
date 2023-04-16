package com.musicbee.utility;

import javafx.stage.Stage;

public class Settings {
    private static final double minWidth = 1050;
    private static final double minHeight = 800;
    private static double width = 950;
    private static double height = 800;

    public static double getMinWidth() {
        return minWidth;
    }
    public static double getMinHeight() {
        return minHeight;
    }
    public static void setWidth(double width) {
        Settings.width = width;
    }
    public static void setMinHeight(double height) {
        Settings.height = height;
    }
    public static void updateWidthHeight(Stage stage) {
        Settings.height = stage.getHeight();
        Settings.width = stage.getWidth();
    }
}
