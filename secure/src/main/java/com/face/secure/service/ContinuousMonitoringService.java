package com.face.secure.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
public class ContinuousMonitoringService {

    @Autowired
    private FaceCamService faceCamService;

    private boolean stillAllTime = true;

    public boolean startMonitoring(int minutes, int timeLimit) {
        long timeLimitMin = timeLimit *= 60000;
        long startTime = System.currentTimeMillis();
        
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

            Runnable task = new Runnable() {
                public void run() {
                    try {
                        if (faceCamService.detectFaces()) {
                            System.out.println("Face detected");
                        } else {
                            stillAllTime = false;
                            scheduler.shutdown();
                            System.out.println("Face not detected");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        stillAllTime = false;
                        scheduler.shutdown();
                    }
                }
            };
            scheduler.scheduleAtFixedRate(task, 0, minutes, TimeUnit.MINUTES);
        
            while(System.currentTimeMillis() - startTime < timeLimitMin){
                int i=1;
                try {
                    if(stillAllTime){
                        Thread.sleep(1000);
                    }else{
                        break;
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        return stillAllTime;
    }
}
