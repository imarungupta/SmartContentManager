package com.smart.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import javax.mail.*;

@Service
public class SpringBootEmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromMail;

    public boolean sendEmail2(String to, String subject, String body) throws NoSuchProviderException {

        System.out.println("To- "+to);
        System.out.println("Subject- "+subject);
        System.out.println("Body- "+body);
        SimpleMailMessage message = new SimpleMailMessage();

        message.setTo(to);
        message.setFrom(fromMail);
        message.setSubject(subject);
        message.setText(body);

       // mailSender.send(message);
        mailSender.send(message);
        return false;
    }
}
