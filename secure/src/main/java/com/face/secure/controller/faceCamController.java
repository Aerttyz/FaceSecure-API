package com.face.secure.controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.face.secure.dtos.DetectFacesDTO;
import com.face.secure.service.FaceCamService;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;



@RestController
@CrossOrigin
public class faceCamController {

    @Autowired
    private FaceCamService faceCamService;

    @GetMapping("/faceCam")
    public String faceCam(DetectFacesDTO detectFacesDTO) throws IOException {
        DetectFacesDTO facesDTO = faceCamService.detectFaces(detectFacesDTO);
        String response = "Face detectada:" + facesDTO.getName() + "confidence" + facesDTO.getConfidence();
        return response;
    }

    @GetMapping("/train")
    public ResponseEntity<?> trainFace() {
        faceCamService.trainFaceRecognizer();
        return ResponseEntity.ok().build();
    }

    @GetMapping("/recognize")
    public String recognize(@RequestParam MultipartFile image) {
        return faceCamService.recognize(image);
    }

    @GetMapping("/addNewData")
    public ResponseEntity<?> addNewData() {
        faceCamService.addNewDataToModel();
        return ResponseEntity.ok().build();
    } 
}
