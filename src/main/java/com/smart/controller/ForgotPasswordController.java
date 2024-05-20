package com.smart.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Random;

@Controller
public class ForgotPasswordController {

    @RequestMapping(value = "/forgotPassword")
    public String openForgotPasswordPage() {
        return "forgotPasswordEmailForm";
    }

    Random random = new Random(1000);

    @PostMapping("/sendOTP")
    public String sendForgotPasswordOTP(@RequestParam("email") String email) {

        System.out.println("Sending password for " + email);
        // Generate OTP of four digit

        int Otp = random.nextInt(9999);
        System.out.println("OTP: " + Otp);
        // We have got OTP, now let's write code to send otp to user's email

        return "verify_OTP";
    }
}
