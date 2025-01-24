package com.face.secure.service;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.core.Size;
import org.opencv.face.LBPHFaceRecognizer;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.videoio.VideoCapture;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.face.secure.dtos.DetectFacesDTO;

@Service
public class FaceCamService {

    private final CascadeClassifier faceDetector;
    @Autowired
    private UserService userService;


    public FaceCamService() throws IOException {
        String cascadePath = new ClassPathResource("haarcascade_frontalface_default.xml").getFile().getAbsolutePath();
        this.faceDetector = new CascadeClassifier(cascadePath);
    }

    public List<Rect> detectFaces(Mat image) {
        MatOfRect faces = new MatOfRect();
        faceDetector.detectMultiScale(image, faces, 1.1, 3, 0, new Size(30, 30), new Size());
        for (Rect face : faces.toArray()) {
            Imgproc.rectangle(image, face, new org.opencv.core.Scalar(0, 255, 0), 2);
        }
        return List.of(faces.toArray());
    }

    public void addNewDataToModel() {
        LBPHFaceRecognizer faceRecognizer = LBPHFaceRecognizer.create();
        //AQUI você deve adicionar o seu caminho para o modelo
        faceRecognizer.read("E:/face.yml");

        //AQUI você deve adicionar o seu caminho para o dataset
        File dataset = new File("E:/dataset");
        File[] labelDirs = dataset.listFiles(File::isDirectory);

        List<Mat> images = new ArrayList<>();
        List<Integer> labels = new ArrayList<>();

        for (File labelDir : labelDirs) {
            int label = Integer.parseInt(labelDir.getName());
            File[] imageFiles = labelDir.listFiles((dir, name) -> name.endsWith(".png"));
            int maxLength = String.valueOf(imageFiles.length).length();
            for (int i = 0; i < imageFiles.length; i++) {
                File oldFile = imageFiles[i];
                String newFileName = String.format("%s/%0" + maxLength + "d.png",
                        labelDir.getAbsolutePath(), i + 1);
                File newFile = new File(newFileName);

                if (!oldFile.renameTo(newFile)) {
                    System.err.println("Erro ao renomear arquivo: " + oldFile.getName());
                }
            }
            imageFiles = labelDir.listFiles((dir, name) -> name.endsWith(".png"));

            for (File imageFile : imageFiles) {
                Mat image = Imgcodecs.imread(imageFile.getAbsolutePath(), Imgcodecs.IMREAD_GRAYSCALE);
                if (!image.empty()) {
                    images.add(image);
                    labels.add(label);
                }
            }
        }

        Mat labelsMat = new Mat(labels.size(), 1, CvType.CV_32SC1);
        for (int i = 0; i < labels.size(); i++) {
            labelsMat.put(i, 0, labels.get(i));
        }

        faceRecognizer.update(images, labelsMat);
        
        //AQUI você deve adicionar o seu caminho para o modelo
        faceRecognizer.save("E:/face.yml");
    }

    public void trainFaceRecognizer() {
        //AQUI você deve adicionar o seu caminho para o dataset
        File dataset = new File("E:/dataset");
        File[] labelDirs = dataset.listFiles(File::isDirectory);

        List<Mat> images = new ArrayList<>();
        List<Integer> labels = new ArrayList<>();

        for (File labelDir : labelDirs) {
            int label = Integer.parseInt(labelDir.getName());
            File[] imageFiles = labelDir.listFiles((dir, name) -> name.endsWith(".png"));


            if (imageFiles == null || imageFiles.length == 0) {
                System.err.println("Nenhuma imagem encontrada no diretório: " + labelDir.getAbsolutePath());
                continue;
            }

            int maxLength = String.valueOf(imageFiles.length).length();
            for (int i = 0; i < imageFiles.length; i++) {
                File oldFile = imageFiles[i];
                String newFileName = String.format("%s/%0" + maxLength + "d.png",
                        labelDir.getAbsolutePath(), i + 1);
                File newFile = new File(newFileName);

                if (!oldFile.renameTo(newFile)) {
                    System.err.println("Erro ao renomear arquivo: " + oldFile.getName());
                }
            }

            imageFiles = labelDir.listFiles((dir, name) -> name.endsWith(".png"));

            for (File imageFile : imageFiles) {
                Mat image = Imgcodecs.imread(imageFile.getAbsolutePath());

                if (image.empty()) {
                    System.err.println("Falha ao carregar a imagem: " + imageFile.getAbsolutePath());
                    continue;
                }
                List<Rect> faces = detectFaces(image);

                for (Rect face : faces) {
                    if (isRectValid(face, image)) {
                        Mat faceImage = new Mat(image, face);

                        if (faceImage.rows() > 0 && faceImage.cols() > 0) {
                            Mat resizedImage = new Mat();
                            Imgproc.resize(faceImage, resizedImage, new Size(150, 150));
                            Imgproc.cvtColor(resizedImage, resizedImage, Imgproc.COLOR_BGR2GRAY);
                            images.add(resizedImage);
                            labels.add(label);
                        } else {
                            System.err.println("A matriz faceImage está vazia ou com dimensões inválidas.");
                        }
                    } else {
                        System.err.println("Rect inválido para a imagem: " + imageFile.getAbsolutePath());
                    }
                }
            }
        }

        LBPHFaceRecognizer faceRecognizer = LBPHFaceRecognizer.create();
        Mat labelsMat = new Mat(labels.size(), 1, CvType.CV_32SC1);
        for (int i = 0; i < labels.size(); i++) {
            labelsMat.put(i, 0, labels.get(i));
        }
        faceRecognizer.train(images, labelsMat);
        faceRecognizer.save("E:/lbph_model.yml");
    }

    private boolean isRectValid(Rect rect, Mat mat) {
        return rect.width > 50 && rect.height > 50 && rect.x >= 0 && rect.y >= 0;
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
    
    public DetectFacesDTO detectFaces(DetectFacesDTO detectFacesDTO) throws IOException {
        boolean faceDetected = false;

        VideoCapture capture = new VideoCapture(0);
        if (!capture.isOpened()) {
            System.out.println("Erro ao abrir a câmera.");
            return null;
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
                    detectFacesDTO.setName(nome);
                    detectFacesDTO.setConfidence(String.valueOf(confidence[0]));
                    detectFacesDTO.setFaceDetected(true);
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
        return detectFacesDTO;
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

}
