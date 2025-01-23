package com.face.secure.service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.core.Size;
import org.opencv.face.FaceRecognizer;
import org.opencv.face.LBPHFaceRecognizer;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.videoio.VideoCapture;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.core.io.Resource;

@Service
public class FaceCamService {

    private final CascadeClassifier faceDetector;

    public FaceCamService() throws IOException {
        String cascadePath = new ClassPathResource("haarcascade_frontalface_default.xml").getFile().getAbsolutePath();
        this.faceDetector = new CascadeClassifier(cascadePath);
    }

    public List<Rect> detectFaces(Mat image) {
        MatOfRect faces = new MatOfRect();
        faceDetector.detectMultiScale(image, faces, 1.1, 3, 0, new Size(30, 30), new Size());
        for(Rect face: faces.toArray()) {
            Imgproc.rectangle(image, face, new org.opencv.core.Scalar(0, 255, 0), 2);
        }
        Imgcodecs.imwrite("detected_faces1.png", image);
        return List.of(faces.toArray());
    }
    public void addNewDataToModel() {
        LBPHFaceRecognizer faceRecognizer = LBPHFaceRecognizer.create();
        faceRecognizer.read("E:/face.yml");
    
        File dataset = new File("E:/dataset");
        File[] labelDirs = dataset.listFiles(File::isDirectory);
    
        List<Mat> images = new ArrayList<>();
        List<Integer> labels = new ArrayList<>();
    
        for (File labelDir : labelDirs) {
            int label = Integer.parseInt(labelDir.getName());
            File[] imageFiles = labelDir.listFiles((dir, name) -> name.endsWith(".png"));
    
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
    
        faceRecognizer.save("E:/face.yml"); 
    }

    public void trainFaceRecognizer() {
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

        Resource resource = new ClassPathResource("haarcascade_frontalface_default.xml");
        CascadeClassifier faceDetector = new CascadeClassifier(resource.getFile().getAbsolutePath());
        if (faceDetector.empty()) {
            System.out.println("Erro ao carregar o Haarcascade.");
            return false;
        }

        LBPHFaceRecognizer faceRecognizer = LBPHFaceRecognizer.create();
        faceRecognizer.read("E:/face.yml");

        Mat frame = new Mat();

        while (true) {

            capture.read(frame);
            if (frame.empty()) {
                throw new RuntimeException("Captured frame is empty");
            }
            Mat grayFrame = new Mat();
            Imgproc.cvtColor(frame, grayFrame, Imgproc.COLOR_BGR2GRAY);
            Imgproc.equalizeHist(grayFrame, grayFrame);

            MatOfRect faces = new MatOfRect();
            faceDetector.detectMultiScale(grayFrame, faces,1.1,3,0,new Size(30,30),new Size());

            for (Rect face : faces.toArray()) {

                Mat faceROI = new Mat(grayFrame, face);

                int[] label = new int[1];
                double[] confidence = new double[1];
                faceRecognizer.predict(faceROI, label, confidence);
                System.out.println("Predição: " + label[0]);
                System.out.println("Confiança: " + confidence[0]);
                if (confidence[0] < 50) {
                    System.out.println("Face detectada: " + label[0] + " - Confidence " + confidence[0]);
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
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        capture.release();
        return faceDetected;
    }

    public String test() {
        LBPHFaceRecognizer faceRecognizer = LBPHFaceRecognizer.create();
        faceRecognizer.read("E:/face.yml");

        // Testar o reconhecimento de uma nova imagem
        Mat testImage = Imgcodecs.imread("E:/Photos-001/test5.png", Imgcodecs.IMREAD_GRAYSCALE);
        if (!testImage.empty()) {
            int[] label = new int[1];
            double[] confidence = new double[1];

            faceRecognizer.predict(testImage, label, confidence);
            
                System.out.println("Predição: " + label[0]);
                System.out.println("Confiança: " + confidence[0]);
                return "predicao" + label[0] + "confianca" + confidence[0];
            
        }
        return "Erro ao carregar a imagem de teste.";
    }

}
