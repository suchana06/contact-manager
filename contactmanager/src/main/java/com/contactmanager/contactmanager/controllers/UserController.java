package com.contactmanager.contactmanager.controllers;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.contactmanager.contactmanager.entities.Contact;
import com.contactmanager.contactmanager.entities.User;
import com.contactmanager.contactmanager.helper.Message;
import com.contactmanager.contactmanager.repository.HomeRepository;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private HomeRepository hm;

    @ModelAttribute
    public void showCommonData(Model m, Principal p) {
        String userName = p.getName();
        User user = this.hm.getUserByUsername(userName);
        m.addAttribute("user", user);
    }

    @RequestMapping(value = "/index")
    public String dashboard(Model m) {
        m.addAttribute("title", "Dashboard");
        return "normaluser/user_dashboard";
    }

    @RequestMapping(value = "/add-contact")
    public String addContactForm(Model m) {
        m.addAttribute("title", "Add Contact");
        m.addAttribute("contact", new Contact());
        return "normaluser/add_contact";
    }

    @PostMapping("/process-contact")
    public String processContactForm(@ModelAttribute Contact ct, 
    Principal p, @RequestParam("profileimage") MultipartFile image, HttpSession session) {
        try {
            if (image.isEmpty()) {

            } else {
                ct.setImage(image.getOriginalFilename());
                File f1 = new ClassPathResource("static/images").getFile();
                Path path= Paths.get(f1.getAbsolutePath()+File.separator+image.getOriginalFilename());
                Files.copy(image.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
                System.out.println("image uploaded");
            }
            User user = hm.getUserByUsername(p.getName());
            ct.setUser(user);
            user.getContacts().add(ct);
            this.hm.save(user);
            //show success flash
            session.setAttribute("message", new Message("Contact saved!!! Add more", "success"));
        } catch (Exception e) {
            System.out.println(e.getMessage());
            session.setAttribute("message", new Message("Something went wrong, try again...", "danger"));
            //show error flash
        }
        return "normaluser/add_contact";
    }
}
