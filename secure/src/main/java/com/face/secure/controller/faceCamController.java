package com.face.secure.controller;

import org.springframework.web.bind.annotation.RestController;

import com.face.secure.service.FaceCamService;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;


@RestController
@CrossOrigin
public class faceCamController {

    @Autowired
    private FaceCamService faceCamService;


    @GetMapping("/faceCam")
    public String faceCam() throws IOException {
        
        if(faceCamService.detectFaces()) {
            return "Face detected";
        }
        
        return "Face not detected";
    }
    
}
