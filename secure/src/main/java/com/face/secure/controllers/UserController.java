package com.face.secure.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.face.secure.model.UserModel;
import com.face.secure.service.UserService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody UserModel userModel) {
        try {
            userService.register(userModel);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }        
        return ResponseEntity.ok().build();
    }
    
}
