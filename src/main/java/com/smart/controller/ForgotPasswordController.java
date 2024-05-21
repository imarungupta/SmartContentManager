package com.smart.controller;

import com.smart.service.EmailService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.mail.MessagingException;
import java.util.Random;

@Controller
public class ForgotPasswordController {

    @Autowired
    private EmailService emailService;

    @RequestMapping(value = "/forgotPassword")
    public String openForgotPasswordPage() {
        return "forgotPasswordEmailForm";
    }

    Random random = new Random(1000);

    @PostMapping("/sendOTP")
    public String sendForgotPasswordOTP(@RequestParam("email") String email, HttpSession session) {

        System.out.println("Sending password for " + email);
        // Generate OTP of four digit

        int Otp = random.nextInt(9999);
        System.out.println("OTP: " + Otp);
        // We have got OTP, now let's write code to send otp to user's email
        String subject = "OTP From SCM.";
        String message = "OTP- "+Otp;
        String to = email;
        try {
            boolean sendEmailFlag = this.emailService.sendEmail(to, subject, email);
            if(sendEmailFlag){
                return "change_password";
            }else {
                session.setAttribute("message","Check Your Email ID");
                return "verify_OTP";
            }
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }


    }
}
