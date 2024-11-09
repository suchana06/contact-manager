package com.contactmanager.contactmanager.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.contactmanager.contactmanager.entities.User;
import com.contactmanager.contactmanager.helper.Message;
import com.contactmanager.contactmanager.repository.HomeRepository;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
public class HomeController {

    @Autowired
    private PasswordEncoder pn;
    @Autowired
    private HomeRepository hm;

    @GetMapping("/")
    public String home(Model m) {
        m.addAttribute("title", "Home page");
        return "home";
    }

    @GetMapping("/about")
    public String about(Model m) {
        m.addAttribute("title", "About us");
        return "about";
    }

    @GetMapping("/signup")
    public String signup(Model m) {
        m.addAttribute("title", "Register here");
        m.addAttribute("user", new User());
        return "signup";
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("user") User user, BindingResult res,
            @RequestParam(value = "agreement", defaultValue = "false") boolean agreement, Model m, HttpSession session) {
        user.setEnabled(true);
        user.setRole("ROLE_USER");
        user.setPassword(pn.encode(user.getPassword()));
        try {
            if (!agreement) {
                throw new Exception();
            }
            if (res.hasErrors()) {
                m.addAttribute("user", user);
                return "signup";
            }
            this.hm.save(user);
            m.addAttribute("user", new User());
            m.addAttribute("session", session);
            m.addAttribute("message", new Message("Successfully Registered!! ", "alert-success"));
            return "signup";
        } catch (Exception e) {
            m.addAttribute("user", user);
            m.addAttribute("session", session);
            m.addAttribute("message", new Message("Accept terms and conditions", "alert-danger"));
            return "signup";
        }
    }

    //custom login
    @RequestMapping("/signin")
    public String customlogin(Model m){
        m.addAttribute("title", "Login page");
        return "login";
    }
}
