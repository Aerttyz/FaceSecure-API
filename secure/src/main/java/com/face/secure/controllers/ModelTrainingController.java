package com.face.secure.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import com.face.secure.service.ModelTrainingService;


@RestController
public class ModelTrainingController {

    @Autowired
    private ModelTrainingService modelTrainingService;

    @GetMapping("/train")
    public ResponseEntity<?> trainFace() {
        modelTrainingService.trainFaceRecognizer();
        return ResponseEntity.ok().build();
    }
    
}
