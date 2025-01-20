package com.face.secure.service;


import java.io.IOException;

import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.videoio.VideoCapture;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

@Service
public class FaceCamService {

    public boolean detectFaces() throws IOException {
        boolean faceDetected = false;
        
        VideoCapture capture = new VideoCapture(0);
        if (!capture.isOpened()) {
            System.out.println("Erro ao abrir a câmera.");
            return false;
        }

        Resource resource = new ClassPathResource("haarcascade_frontalface_default.xml");
        CascadeClassifier faceDetector = new CascadeClassifier(resource.getFile().getAbsolutePath());

        Mat frame = new Mat();

        while (true) {

            capture.read(frame);
            if (frame.empty()) {
                throw new RuntimeException("Captured frame is empty");
            }
            
            Imgproc.cvtColor(frame, frame, Imgproc.COLOR_BGR2GRAY);

            MatOfRect faces = new MatOfRect();
            faceDetector.detectMultiScale(frame, faces);

            if(!faces.empty()) {
                try {
                    Thread.sleep(1000);
                    faceDetected = true;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                break;
            }
            try {
                Thread.sleep(100);  // Pequeno atraso para não sobrecarregar a CPU
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        capture.release();
        return faceDetected;

    }
    
}
