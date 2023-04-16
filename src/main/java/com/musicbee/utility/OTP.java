package com.musicbee.utility;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import java.util.Properties;
import java.util.Random;

public class OTP {

    private static final String USERNAME = "musicbee042534@gmail.com";
    private static final String PASSWORD = "Alhamdulillah";
    private static final int OTP_LENGTH = 6;

    public static String sendEmail(String address) {

        Properties properties = new Properties();
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.port", "587");
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");

        Session session = Session.getInstance(properties, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(USERNAME, PASSWORD);
            }
        });

        String numbers = "0123456789";
        Random random = new Random();
        char[] otp = new char[OTP_LENGTH];

        for (int i = 0; i < OTP_LENGTH; i++) {
            otp[i] = numbers.charAt(random.nextInt(numbers.length()));
        }

        String OTP = new String(otp);

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(USERNAME));
            message.setRecipients(
                    Message.RecipientType.TO,
                    InternetAddress.parse(address)
            );
            message.setSubject("OTP for registration to Music Bee");
            message.setText("Dear user,\n\nWelcome to Music Bee!\n\nYour OTP is " + OTP +
                    ". Please use the OTP to register to Music Bee.");

            Transport.send(message);

            System.out.println("Email sent successfully.");
        } catch (MessagingException e) {
            e.printStackTrace();
        }

        return OTP;
    }
}
