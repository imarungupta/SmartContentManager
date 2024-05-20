package com.smart.controller;

import com.smart.controller.helper.Message;
import com.smart.dao.UserRepository;
import com.smart.entities.User;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Controller
public class HomeController {

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    @Autowired
    private UserRepository userRepository;

    @RequestMapping("/")
    public String home(Model model){
        model.addAttribute("title","Home-SmartUserContact");
        return "home";
    }

    @RequestMapping("/about")
    public String about(Model model){
        model.addAttribute("title","About-SCM");
        return "about";
    }
    @RequestMapping("/signup")
    public String signUp(Model model){
        model.addAttribute("title","Signup-SCM");
        model.addAttribute("user",new User());
        return "signup";
    }

    // Handler for registration
    @RequestMapping(value = "/do_register",method = RequestMethod.POST)
    public String registerUser(@Valid @ModelAttribute("user") User user, BindingResult bindingResult,
                               @RequestParam(value = "agreement",
                               defaultValue = "false") boolean agreement,
                               Model model, HttpSession session){
        // @ModelAttribute("user") User user : this take all the input from the ModelAttribute and put into user object.
        // Additionally, we can set other values in User object
        // @Valid and BindingResult bindingResult is used for server side validation
        try {
            System.out.println("Agreement: "+agreement);
            System.out.println("USER: "+user);
            if(!agreement){    // If agreement did not checked then throw exception with message, else save the user in database
                System.out.println("You have not agreed the terms and condition");
                throw new Exception("You have not agreed the terms and condition");
            }
            if(bindingResult.hasErrors()){
                // if errors comes then whatever data we have in user object send it back to form so that we should not write it again
                System.out.println("bindingResult: "+bindingResult.toString());
                model.addAttribute("user",user);
                // return the signup form again
                return "signup";
            }
            user.setRole("ROLE_USER");
            user.setEnabled(true);
            user.setImageUrl("default.png");
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            User userResult = userRepository.save(user);
            // Once the message inserted then make the all object value blank so will send empty object of User
            model.addAttribute("user",new User());
            session.setAttribute("message",new Message("Successfully Registered !!","alert-success"));
            // then return the signup page. If we want to show some other page, just change the page name in return
            return "signup";
        }catch (Exception e){
            e.printStackTrace();
            model.addAttribute("user", user);
            session.setAttribute("message",new Message("Something went wrong!"+e.getMessage(),"alert-danger"));
            return "signup";
        }
    }

    //Handler for login
    @GetMapping("/signin")
    public String customLogin(Model model){
        model.addAttribute("title","sign-in Page");
        return "signin";
    }



}
