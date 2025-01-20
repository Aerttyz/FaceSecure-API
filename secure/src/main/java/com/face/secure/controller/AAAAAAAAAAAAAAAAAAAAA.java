package com.face.secure.controller;

import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.objdetect.CascadeClassifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@RestController
@RequestMapping("/face")
public class AAAAAAAAAAAAAAAAAAAAA {

    private static final String CASCADE_FILE = "src\\main\\resources\\haarcascade_frontalface_default.xml";

    @PostMapping("/detect")
    public ResponseEntity<String> detectFaces(@RequestParam("file") MultipartFile file) throws IOException {
        // Salvar arquivo recebido para leitura
        File tempFile = File.createTempFile("uploaded-", ".jpg");
        file.transferTo(tempFile);

        // Carregar a imagem
        Mat image = Imgcodecs.imread(tempFile.getAbsolutePath());

        // Verificar se a imagem foi carregada corretamente
        if (image.empty()) {
            return ResponseEntity.badRequest().body("Erro ao carregar a imagem.");
        }

        // Carregar o classificador Haar
        CascadeClassifier faceDetector = new CascadeClassifier(CASCADE_FILE);
        if (faceDetector.empty()) {
            return ResponseEntity.status(500).body("Erro ao carregar o classificador Haar.");
        }

        // Detectar rostos
        MatOfRect faceDetections = new MatOfRect();
        faceDetector.detectMultiScale(image, faceDetections);

        // Contar rostos detectados
        Rect[] facesArray = faceDetections.toArray();
        return ResponseEntity.ok("Rostos detectados: " + facesArray.length);
    }
}
