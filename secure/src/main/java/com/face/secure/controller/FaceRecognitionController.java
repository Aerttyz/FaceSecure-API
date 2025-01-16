package com.face.secure.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.face.secure.service.FaceRecognitionService;

@RestController
@RequestMapping("/api/face-recognition")
public class FaceRecognitionController {

    private final FaceRecognitionService faceRecognitionService;

    // Injeção de dependência através do construtor
    public FaceRecognitionController(FaceRecognitionService faceRecognitionService) {
        this.faceRecognitionService = faceRecognitionService;
    }

    // Endpoint para reconhecimento de rosto a partir de uma imagem
    @PostMapping("/recognize")
    public ResponseEntity<String> recognizeFace(@RequestParam("image") MultipartFile image) {
        try {
            // Chama o serviço para realizar o reconhecimento facial
            String result = faceRecognitionService.recognizeFace(image);
            return ResponseEntity.ok(result); // Retorna a resposta com o status 200 OK e o resultado
        } catch (Exception e) {
            // Caso ocorra um erro, retorna a mensagem de erro com status 500
            return ResponseEntity.status(500).body("Erro ao processar a imagem: " + e.getMessage());
        }
    }
}
