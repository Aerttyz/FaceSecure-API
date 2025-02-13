package com.face.secure.service;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.io.File;
import java.util.List;

import org.opencv.core.Core;
import org.opencv.objdetect.CascadeClassifier;
import org.springframework.core.io.ClassPathResource;
import org.opencv.core.Rect;
import org.opencv.core.Size;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.face.LBPHFaceRecognizer;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


@Service
public class FaceRecognitionService {

    @Autowired
    private UserService userService;
    private final CascadeClassifier faceDetector;

    public FaceRecognitionService() throws IOException {
        String cascadePath = new ClassPathResource("haarcascade_frontalface_default.xml").getFile().getAbsolutePath();
        this.faceDetector = new CascadeClassifier(cascadePath);
    }

    public String recognize(MultipartFile image) {
        String pathImge;
        LBPHFaceRecognizer faceRecognizer = LBPHFaceRecognizer.create();
        faceRecognizer.read("E:/face.yml");
        try{
            pathImge = saveImage(image);
        }catch(Exception e){
            e.printStackTrace();
            return "Erro ao salvar a imagem.";
            
        }
        // Testar o reconhecimento de uma nova imagem
        Mat testImage = Imgcodecs.imread(pathImge, Imgcodecs.IMREAD_GRAYSCALE);
        if (!testImage.empty()) {
            int[] label = new int[1];
            double[] confidence = new double[1];

            faceRecognizer.predict(testImage, label, confidence);
            String nome = userService.getNameByLabel(label[0]);
            if(confidence[0] < 30){
                return "predicao" + nome + "confianca" + confidence[0];
            }else{
                return "Face não reconhecida." + confidence[0];
            }
        }
        return "Erro ao carregar a imagem de teste.";
    }

    private String saveImage(MultipartFile image) throws IllegalStateException, IOException{
        
        File file = new File("E:/test");
        if(!file.exists()){
            file.mkdir();
        }
        String fileName = image.getOriginalFilename();
        File dest = new File(file, fileName);

        image.transferTo(dest);
        return dest.getAbsolutePath();
    }

    private void saveFrameForDebugging(Mat frame) {
        
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String fileName = "E:/frames/debug_frame_" + timestamp + ".png";

        // Salvar o frame como imagem
        Imgcodecs.imwrite(fileName, frame);
        System.out.println("Frame salvo: " + fileName);
    }

    public boolean detectFaces() throws IOException {
        boolean faceDetected = false;

        VideoCapture capture = new VideoCapture(0);
        if (!capture.isOpened()) {
            System.out.println("Erro ao abrir a câmera.");
            return false;
        }

        LBPHFaceRecognizer faceRecognizer = LBPHFaceRecognizer.create();
        faceRecognizer.read("E:/face.yml");

        Mat frame = new Mat();
        long timelimit = 59000;
        long startTime = System.currentTimeMillis();
        while (true) {

            if (System.currentTimeMillis() - startTime > timelimit) {
                System.out.println("Time limit.");
                break;
            }
            capture.read(frame);
            if (frame.empty()) {
                throw new RuntimeException("Captured frame is empty");
            }
            //saveFrameForDebugging(frame);
            List<Mat> channels = new ArrayList<>();
            Core.split(frame, channels);
            Mat grayFrame = channels.get(0);
            Imgproc.equalizeHist(grayFrame, grayFrame);
            //saveFrameForDebugging(grayFrame);
            MatOfRect faces = new MatOfRect();
            faceDetector.detectMultiScale(grayFrame, faces, 1.1, 3, 0, new Size(30, 30), new Size());
            
            for (Rect face : faces.toArray()) {
                Mat faceROI = new Mat(grayFrame, face);
                int[] label = new int[1];
                double[] confidence = new double[1];
                faceRecognizer.predict(faceROI, label, confidence);
                String nome = userService.getNameByLabel(label[0]);
                System.out.println("Predição: " + nome);
                System.out.println("Confiança: " + confidence[0]);
                if (confidence[0] < 55) {
                    System.out.println("Face detectada: " + nome + " - Confidence " + confidence[0]);
                    faceDetected = true;
                    break;
                } else {
                    System.out.println("Face não reconhecida.");
                }
            }
            if (faceDetected) {
                break;
            }

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        capture.release();
        return faceDetected;
    }
}
