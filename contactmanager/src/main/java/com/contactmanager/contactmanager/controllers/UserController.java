package com.contactmanager.contactmanager.controllers;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.contactmanager.contactmanager.entities.Contact;
import com.contactmanager.contactmanager.entities.User;
import com.contactmanager.contactmanager.helper.Message;
import com.contactmanager.contactmanager.repository.ContactRepository;
import com.contactmanager.contactmanager.repository.HomeRepository;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private HomeRepository hm;

    @Autowired
    private ContactRepository cm;

    @Autowired
    private BCryptPasswordEncoder bp;

    @ModelAttribute
    public void showCommonData(Model m, Principal p) {
        String userName = p.getName();
        User user = this.hm.getUserByUsername(userName);
        m.addAttribute("user", user);
    }

    //user dashboard page
    @RequestMapping(value = "/index")
    public String dashboard(Model m) {
        m.addAttribute("title", "Dashboard");
        return "normaluser/user_dashboard";
    }

    // add contact form
    @RequestMapping(value = "/add-contact")
    public String addContactForm(Model m) {
        m.addAttribute("title", "Add Contact");
        m.addAttribute("contact", new Contact());
        return "normaluser/add_contact";
    }

    // contact will be posted here for saving in db
    @PostMapping("/process-contact")
    public String processContactForm(@ModelAttribute Contact ct,
            Principal p, @RequestParam("profileimage") MultipartFile image, HttpSession session) {
        try {
            if (image.isEmpty()) {
                ct.setImage("contact.png");
            } else {
                ct.setImage(image.getOriginalFilename());
                File f1 = new ClassPathResource("static/images").getFile();

                Path path = Paths.get(f1.getAbsolutePath() + File.separator + image.getOriginalFilename());
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

    //viewing contacts
    @GetMapping("/view-contact")
    public String showContacts(Model m, Principal p) {
        String name = p.getName();
        User user = this.hm.getUserByUsername(name);
        List<Contact> contacts = this.cm.findContactsByUser(user.getId());
        m.addAttribute("contacts", contacts);
        m.addAttribute("title", "View all contacts");
        return "normaluser/view_contact";
    }

    //listing details of a single contact
    @GetMapping("/contact/{id}")
    public String viewDetails(@PathVariable("id") int id, Model m, Principal p) {
        Optional<Contact> contactdetails = this.cm.findById(id);
        Contact contact = contactdetails.get();
        String usrname = p.getName();
        User user = this.hm.getUserByUsername(usrname);
        if (user.getId() == contact.getUser().getId()) {
            m.addAttribute("contact", contact);
        }
        m.addAttribute("title", "Contact details");
        System.out.println(id);
        return "normaluser/contact_details";
    }

    // deleting a contact
    @GetMapping("/delete/{id}")
    public String deleteContact(@PathVariable("id") int id, Model m, Principal p, HttpSession session) {
        String username = p.getName();
        User user = this.hm.getUserByUsername(username);
        Optional<Contact> contactOptional = this.cm.findById(id);
        Contact contact = contactOptional.get();
        if (user.getId() == contact.getUser().getId()) {
            contact.setUser(null);
            this.cm.delete(contact);
            session.setAttribute("message", new Message("contact deleted successfully", "success"));
        }
        m.addAttribute("title", " Delete Contact");
        return "redirect:/user/view-contact";
    }

    //opening update contact details form
    @PostMapping("/update/{id}")
    public String updateForm(@PathVariable("id") int id, Model m) {
        m.addAttribute("title", "Update contact");
        Contact contact = this.cm.findById(id).get();
        m.addAttribute("contact", contact);
        return "normaluser/updateform";
    }

    // processing update operation of contact details
    @PostMapping("/process-update")
    public String processUpdate(
            @ModelAttribute Contact contact,
            @RequestParam("profileimage") MultipartFile file,
            Model m,
            Principal p,
            HttpSession session) {
        Contact oldContact = this.cm.findById(contact.getcId()).get();
        try {
            if (!file.isEmpty()) {
                File deletefile = new ClassPathResource("static/images").getFile();
                File f2 = new File(deletefile,oldContact.getImage());
                f2.delete();
                contact.setImage(file.getOriginalFilename());
                File f1 = new ClassPathResource("static/images").getFile();
                Path path = Paths.get(f1.getAbsolutePath() + File.separator + file.getOriginalFilename());
                Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

            } else {
                contact.setImage(oldContact.getImage());
            }
            User user = this.hm.getUserByUsername(p.getName());
            contact.setUser(user);
            this.cm.save(contact);
            session.setAttribute("message", new Message("contact details updated successfully","success"));
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return "redirect:/user/contact/"+contact.getcId();
    }

    // profile page
    @GetMapping("/profile")
    public String showProfile(Model m, Principal p){
        User user = this.hm.getUserByUsername(p.getName());
        m.addAttribute("user",user);
        m.addAttribute("title", "Your Profile");
        return "normaluser/profile";
    }

    //settings page
    @GetMapping("/settings")
    public String settings(Model m){
        m.addAttribute("title","Settings Page");
        return "normaluser/settings";
    }
    @PostMapping("/change-pass")
    public String changepass(@RequestParam("old") String oldpass, @RequestParam("new") String newpass, Principal p, HttpSession session){
        System.out.println(oldpass+" "+newpass);
        User user = this.hm.getUserByUsername(p.getName());
        if(bp.matches(oldpass, user.getPassword())){
            //change pass
            user.setPassword(this.bp.encode(newpass));
            this.hm.save(user);
            session.setAttribute("message", new Message("Password Updated successfully","success"));
        }else{
            session.setAttribute("message", new Message("Wrong password entered","danger"));
            return "redirect:/user/settings";
        }
        return "redirect:/user/index";
    }
}
