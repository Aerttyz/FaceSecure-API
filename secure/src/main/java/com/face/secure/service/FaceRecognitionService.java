package com.face.secure.service;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.io.File;

import org.opencv.core.Mat;
import org.opencv.face.LBPHFaceRecognizer;
import org.opencv.imgcodecs.Imgcodecs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


@Service
public class FaceRecognitionService {

    @Autowired
    private UserService userService;

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
}
