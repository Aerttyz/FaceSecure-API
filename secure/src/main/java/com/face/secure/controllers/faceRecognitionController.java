package com.face.secure.controllers;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.face.secure.dtos.DetectFacesDTO;
import com.face.secure.service.FaceRecognitionService;

@RestController
public class faceRecognitionController {

    @Autowired
    private FaceRecognitionService faceRecognitionService;

    @GetMapping("/recognize")
    public String recognize(@RequestParam MultipartFile image) {
        return faceRecognitionService.recognize(image);
    }
    
    @GetMapping("/faceCam")
    public String faceCam(DetectFacesDTO detectFacesDTO) throws IOException {
        DetectFacesDTO facesDTO = faceRecognitionService.detectFaces(detectFacesDTO);
        String response = "Face detectada:" + facesDTO.getName() + "confidence" + facesDTO.getConfidence();
        return response;
    }
}
