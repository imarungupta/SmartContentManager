package com.smart.controller;

import com.smart.dao.ContactRepository;
import com.smart.dao.UserRepository;
import com.smart.entities.Contact;
import com.smart.entities.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

// Step 5: Search Functionality

@RestController
public class SearchController {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ContactRepository contactRepository;
    // Search Handler
    @GetMapping("search/{query}")
    public ResponseEntity<?> search(@PathVariable String query, Principal principal){

        principal.getName();
        User userName = userRepository.getUserByUserName(principal.getName());
        List<Contact> contactList = contactRepository.findByNameContainingAndUser(query, userName);

        return ResponseEntity.of(Optional.ofNullable(contactList));
    }
}
