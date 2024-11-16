package com.contactmanager.contactmanager.controllers;

import java.security.SecureRandom;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.contactmanager.contactmanager.entities.User;
import com.contactmanager.contactmanager.helper.Message;
import com.contactmanager.contactmanager.repository.HomeRepository;
import com.contactmanager.contactmanager.service.EmailService;

import jakarta.servlet.http.HttpSession;

@Controller
public class ForgotController {

    @Autowired
    private HomeRepository hm;
    @Autowired
    private EmailService ems;
    @Autowired
    private BCryptPasswordEncoder bcrypt;

    SecureRandom secureRandom = new SecureRandom();

    //forgot password form
    @RequestMapping("/forgetpass")
    public String forgotpass(Model m) {
        m.addAttribute("title", "Forgot Password");
        return "forgotpass";
    }

    @PostMapping("/send-otp")
    public String verifyotp(@RequestParam("email") String email, HttpSession session) {
        String subject = "Here's your OTP for changing password";
        int otp = secureRandom.nextInt(900000) + 100000;

        String msg = "Copy this otp: " + otp;
        String to = email;
        String from = "singhsuchana170@gmail.com";
        boolean result = this.ems.sendemail(subject, msg, to, from);
        if (result) {
            session.setAttribute("otp", otp);
            session.setAttribute("myemail", email);
            session.setAttribute("message", new Message("check your mail for otp", "success"));
            System.out.println("otp sent successfully");
        } else {
            System.out.println("something went wrong!! try again");
            session.setAttribute("message", new Message("something went wrong!! try again", "danger"));
            return "forgotpass";
        }
        return "verify_otp";
    }

    @PostMapping("/verify-otp")
    public String verifyfOtp(@RequestParam("otp") int otp, HttpSession session) {
        int myotp = (int) session.getAttribute("otp");
        String myemail = (String) session.getAttribute("myemail");
        if (myotp == otp) {
            //verify email
            User user = this.hm.getUserByUsername(myemail);
            if (user == null) {
                session.setAttribute("message", new Message("no such user exists!!!", "danger"));
                return "forgotpass";
            }
            return "change_pass_form";
        } else {
            session.setAttribute("message", new Message("You entered wrong OTP", "danger"));
            return "verify_otp";
        }
    }

    @PostMapping("/change-pass")
    public String changepass(@RequestParam("newPassword") String newpass, HttpSession session) {
        String email = (String) session.getAttribute("myemail");
        User user = this.hm.getUserByUsername(email);
        user.setPassword(this.bcrypt.encode(newpass));
        this.hm.save(user);
        System.out.println("update successful");
        session.setAttribute("changed", new Message("Password changed successfully! Login to continue", "success"));
        return "redirect:/signin";
    }

}
