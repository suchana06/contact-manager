package com.contactmanager.contactmanager.controllers;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.contactmanager.contactmanager.entities.User;
import com.contactmanager.contactmanager.repository.HomeRepository;

@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private HomeRepository hm;

    @RequestMapping(value = "/index")
    public String dashboard(Model m, Principal p){
        String userName = p.getName();
        System.out.println(userName);
        User user= this.hm.getUserByUsername(userName);
        System.out.println(user);
        m.addAttribute("user",user);
        return "normaluser/user_dashboard";
    }
}
