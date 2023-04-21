package com.musicbee.utility;

import com.musicbee.entities.Song;
import javafx.event.ActionEvent;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.ImageView;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.shape.Circle;

import java.io.File;

public class Tools {
    private static final int MAX_PASSWORD_LENGTH = 32;
    private static final long MIN_HASH = 4294967295L;
    private static final char EXTRA_CHARS = 1;
    public static long hashPassword(String password) {
        StringBuilder passwordBuilder = new StringBuilder(password);
        passwordBuilder.append(String.valueOf(EXTRA_CHARS).repeat(Math.max(0, MAX_PASSWORD_LENGTH - passwordBuilder.length())));
        password = passwordBuilder.toString();
        long sum = 0;
        long powOf2 = 1;
        for(int i = 0; i < password.length(); i++) {
            sum += (password.charAt(i) * powOf2);
            powOf2 *= 2;
        }
        sum -= MIN_HASH;
//        System.out.println(sum);
        return sum;
    }
    public static String timeToString(double millis) {
        int millisecond = (int) Math.round(millis);
        int seconds = millisecond / 1000;
        int minutes = seconds / 60;
        seconds %= 60;
        return (String.format("%02d", minutes) + ":" + String.format("%02d", seconds));
    }
    public static int calcStrength(String hello){
        //1 for weak, 2 medium, 3 for strong
        int ct=0;
        int count=hello.length();
        if(hello.length()<12) return 1;
        int[] arr= new int[4];
        arr[0]=0; arr[1]=0; arr[2]=0;   arr[3]=0; //smol capital special number
        for(int i=0;i<hello.length();i++){
            if(48<=hello.charAt(i) && hello.charAt(i)<=57){ //if(arr[3]==0)
                {
                    //System.out.println("lol");
                    arr[3]++;} ct++;}
            if(65<=hello.charAt(i) && hello.charAt(i)<=90){ //if(arr[1]==0)
                {arr[1]++;} ct++;}
            if(97<=hello.charAt(i) && hello.charAt(i)<=122){ //if(arr[0]==0)
                {arr[0]++;}    ct++;}
        }
        arr[2]=count-ct;
        //System.out.println(arr[0]+" "+arr[1]+" "+arr[2]+" "+arr[3]);
        if(arr[0]>=2 && arr[1]>=2 && arr[2]>=2 && arr[3]>=2) return 3;
        if(arr[0]>=1 && arr[1]>=1 && arr[2]>=1 && arr[3]>=1) return 2;
        //System.out.println("lol");
        return 1;
    }

    public static void getLengthOfSong(Song song) {
        Media media = new Media( (new File(song.getPath())).toURI().toString() );
        MediaPlayer player = new MediaPlayer( media );

        player.setOnReady(()-> {
            double totalTime = player.getTotalDuration().toMillis();
            song.setLength(totalTime);
//                System.out.println(totalTime);
            player.pause();
        });
    }

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

    public static void clipImageview(ImageView imageView, double diameter) {
        imageView.setFitHeight(diameter);
        imageView.setFitWidth(diameter);
        imageView.setPreserveRatio(false);
        Circle circle = new Circle(diameter / 2, diameter / 2, diameter / 2);
        imageView.setClip(circle);
    }
}
