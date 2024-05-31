package com.smart.controller;

import org.springframework.stereotype.Service;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

@Service
public class EmailService {


    public boolean sendEmail(String to, String subject, String msgBody) throws MessagingException {

        boolean result = false;
        String from = "email.service.test06@gmail.com";
        // variable for gmail
        String host= "smtp.gmail.com";

        Properties props = new Properties();
        // Step-1 Setting Gmail, Host, authentication port and ssl in the property to be used further

        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", "465"); // 587 465
        props.put("mail.smtp.ssl.enable", "true");
        props.put("mail.smtp.auth", "true");

         /* The below properties are not required

        props.put("mail.smtp.ssl.protocols", "TLSv1.2");
        props.put("mail.smtp.ssl.trust", "smtp.gmail.com");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.transport.protocol" , "smtp");
        */

        // Testing SMTP Server issue
        // https://www.youtube.com/watch?v=kTcmbZqNiGw

        // Step2- Create a session object to send email in that particular session
        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication("email.service.test06@gmail.com","vfwdjuntopgvxefg");
            }
        });

        session.setDebug(true); // Just to debug the process
        Transport sessionTransport = session.getTransport("smtp");
       // sessionTransport.connect(host,"email.service.test06@gmail.com","vfwdjuntopgvxefg");
        //Step3- Compose the message , [Text, Multimedia]
        MimeMessage mimeMessage= new MimeMessage(session);
        try {
            mimeMessage.setFrom("email.service.test06@gmail.com");
            mimeMessage.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
            mimeMessage.setSubject(subject);
            //mimeMessage.setText(msgBody); Here the message is going in just text format to send into HTML format
            mimeMessage.setContent(msgBody,"text/html"); // Now message will go into HTML form



            // Step-4: Send the message using transport class
            Transport.send(mimeMessage);
            System.out.println(" Email is sent successfully !! ");

        } catch (MessagingException e) {
           // throw new MessagingException();
            e.printStackTrace();
        }
        return true;
    }
}
