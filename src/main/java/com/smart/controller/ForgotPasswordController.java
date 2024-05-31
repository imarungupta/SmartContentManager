package com.smart.controller;

import com.smart.dao.UserRepository;
import com.smart.entities.User;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.mail.MessagingException;
import java.security.Principal;
import java.util.Random;

@Controller
public class ForgotPasswordController {

    @Autowired
    private SpringBootEmailService emailService2;
    @Autowired
    private EmailService emailService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @RequestMapping(value = "/forgotPassword")
    public String openForgotPasswordPage(Model model) {
        model.addAttribute("title","ForgotPasswordEmailForm");
        return "forgotPasswordEmailForm";
    }

    // Send OTP to email
    Random random = new Random(1000);

    @PostMapping("/sendOTP")
    public String sendForgotPasswordOTP(@RequestParam("email") String toEmail, HttpSession session,Model model) throws MessagingException {

        System.out.println("Sending password for " + toEmail);
        // Generate OTP of four digit

        int Otp = random.nextInt(9999);
        System.out.println("OTP: " + Otp);
        // We have got OTP, now let's write code to send otp to user's email
        String subject = "OTP From SCM.";
        //String message = "OTP- "+Otp;
        String message =""
                    +"<div style ='border:1px solid #e2e2e2; padding :20px'>"
                    +"<h1>"
                    +"OTP is="
                    +"<b>"+Otp
                    +"</b>"
                    +"</h1>"
                    +"</div>";
        String to = toEmail;
        boolean sendEmailFlag = this.emailService.sendEmail(to, subject, message);
        //boolean sendEmailFlag = this.emailService2.sendEmail2(to, subject, message);
        if(sendEmailFlag){
            model.addAttribute("title","Verify_OTP");
            // Now let's keep the OTP and email in the DB or session for further verification, to make easy now we are keeping into session
            session.setAttribute("sessionOtp",Otp);
            session.setAttribute("sessionToEmail",toEmail);

            // Now when the user, after getting OTP in this email, will put to get it verified
            // then that time we will compare with sessionOtp and if matched then, will fetch the user email to get it verified
            return "verify_OTP";

        }else {
            session.setAttribute("message","Check Your Email ID");
            model.addAttribute("title","ForgotPasswordEmailForm");
            return "forgotPasswordEmailForm";
        }
    }
    // Verify email and OTP from the session and sent into email
    @PostMapping("/verifyOTP")
    public String verifyOTP(@RequestParam("otp") int otp, HttpSession session, Principal principal,Model model){
        int sessionOtp = (int) session.getAttribute("sessionOtp");
        String sessionToEmail = (String) session.getAttribute("sessionToEmail");
        if(sessionOtp==otp){
            User user = this.userRepository.getUserByUserName(sessionToEmail);
            if(user==null){
                // Send error msg
                session.setAttribute("message","User doest not existing with this email. ");
                model.addAttribute("title","forgotPasswordEmailForm");
                return "forgotPasswordEmailForm";
            }else{
                model.addAttribute("title","password_change_form");
                return "password_change_form";
            }

        }else {
            session.setAttribute("message","You have entered wrong OTP !");
            return "verify_OTP";
        }
    }
    @PostMapping("/changePassword")
    public String changePassword(@RequestParam("newpassword") String newPassword, Model model,HttpSession session){
        // First get the email from session
        String sessionToEmail = (String) session.getAttribute("sessionToEmail");
        // get the user from this email
        User user = this.userRepository.getUserByUserName(sessionToEmail);
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        return "redirect:/signin?change=Password changed successfully";
    }
}
