package com.contactmanager.contactmanager.service;

import java.util.Properties;

import org.springframework.stereotype.Service;

import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {

    public boolean sendemail(String subject, String msg, String to, String from) {
        boolean flag = false;
        Properties pr = System.getProperties();
        String host = "smtp.gmail.com";
        pr.put("mail.smtp.host", host);
        pr.put("mail.smtp.port", "587");
        pr.put("mail.smtp.auth", "true");
        pr.put("mail.smtp.starttls.enable", "true");

        Session session = Session.getInstance(pr, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("singhsuchana170@gmail.com", "_yourpassword__");
            }
        });
        session.setDebug(true);
        try {
            MimeMessage m = new MimeMessage(session);
            m.setRecipient(Message.RecipientType.TO, new InternetAddress(to));
            m.setFrom(new InternetAddress(from));
            m.setSubject(subject);
            m.setText(msg);
            Transport.send(m);
            flag = true;

        } catch (Exception e) {
            System.out.println("Error occurred: "+e.getMessage());
        }
        return flag;
    }

}
