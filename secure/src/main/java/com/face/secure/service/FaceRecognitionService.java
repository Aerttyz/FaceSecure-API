package com.face.secure.service; // Certifique-se de que este é o caminho correto do pacote

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.io.IOException;

@Service
public class FaceRecognitionService {

    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME); // Carrega o OpenCV
    }

    public String recognizeFace(MultipartFile image) {
        try {
            // Converte o MultipartFile para Mat (imagem OpenCV)
            byte[] bytes = image.getBytes();
            MatOfByte matOfByte = new MatOfByte(bytes);
            Mat imgMat = Imgcodecs.imdecode(matOfByte, Imgcodecs.IMREAD_COLOR);

            // Aqui você pode implementar o reconhecimento facial com OpenCV ou outras APIs
            // Para fins de exemplo, converte a imagem para escala de cinza
            Mat grayImg = new Mat();
            Imgproc.cvtColor(imgMat, grayImg, Imgproc.COLOR_BGR2GRAY);

            // Salva a imagem convertida para testes
            Imgcodecs.imwrite("recognized_face.jpg", grayImg);

            return "Reconhecimento concluído com sucesso!";
        } catch (IOException e) {
            e.printStackTrace();
            return "Erro ao processar a imagem.";
        }
    }
}
