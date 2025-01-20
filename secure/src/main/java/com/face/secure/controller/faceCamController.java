package com.face.secure.controller;

import org.springframework.web.bind.annotation.RestController;

import com.face.secure.service.FaceCamService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;


@RestController
@CrossOrigin
public class faceCamController {

    @Autowired
    private FaceCamService faceCamService;


    @PostMapping("/faceCam")
    public String faceCam() {
        
        if(faceCamService.detectFaces()) {
            return "Face detected";
        }
        
        return "Face not detected";
    }
    
}
