package com.face.secure.controller;

import org.springframework.web.bind.annotation.RestController;
import org.opencv.core.Core;
import org.springframework.web.bind.annotation.GetMapping;


@RestController
public class TestController {

    @GetMapping("/test")
    public String getMethodName() {
        return "Versão opencv: " + Core.VERSION;
    }
    
}
