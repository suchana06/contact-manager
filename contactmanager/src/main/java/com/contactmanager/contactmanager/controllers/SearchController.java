package com.contactmanager.contactmanager.controllers;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.contactmanager.contactmanager.entities.Contact;
import com.contactmanager.contactmanager.entities.User;
import com.contactmanager.contactmanager.repository.ContactRepository;
import com.contactmanager.contactmanager.repository.HomeRepository;

@RestController
public class SearchController {

    @Autowired
    private HomeRepository hm;
    @Autowired
    private ContactRepository cm;

    @GetMapping("/search/{query}")
    public ResponseEntity<?> search(@PathVariable("query") String query, Principal principal)
    {
        User user = this.hm.getUserByUsername(principal.getName());
        List<Contact> contacts= this.cm.findByNameContainingAndUser(query, user);
        return ResponseEntity.ok(contacts);
    }

}
