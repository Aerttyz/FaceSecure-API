package com.face.secure.service;


import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.videoio.VideoCapture;
import org.springframework.stereotype.Service;

@Service
public class FaceCamService {

    public boolean detectFaces() {
        boolean faceDetected = false;
        
        VideoCapture capture = new VideoCapture(0);
        if (!capture.isOpened()) {
            System.out.println("Erro ao abrir a câmera.");
            return false;
        }

        CascadeClassifier faceDetector = new CascadeClassifier("src/main/resources/haarcascade_frontalface_default.xml");

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
