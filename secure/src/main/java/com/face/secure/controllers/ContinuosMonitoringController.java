package com.face.secure.controllers;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import com.face.secure.dtos.ContinuousMonitoringDTO;
import com.face.secure.service.ContinuousMonitoringService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
public class ContinuosMonitoringController {
    
    @Autowired
    private ContinuousMonitoringService continuousMonitoringService;

    @GetMapping("/startMonitoring")
    public String startMonitoring(@RequestBody ContinuousMonitoringDTO ctMonitoringDTO) throws IOException {
        if(continuousMonitoringService.startMonitoring(ctMonitoringDTO.getMinutes(), ctMonitoringDTO.getTimeLimit())){
            return "Still all time";
        }else{
            return "Not all time";
        }
    }
}
