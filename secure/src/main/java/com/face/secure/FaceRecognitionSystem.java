import org.opencv.core.*;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.videoio.VideoCapture;
import org.opencv.imgproc.Imgproc;
import org.opencv.highgui.HighGui;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.Base64;
import javax.swing.*;

public class FaceRecognitionSystem {

    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME); // Carregar biblioteca OpenCV
    }

    public static void main(String[] args) {
        // Configurar banco de dados
        String dbUrl = "jdbc:mysql://localhost:3306/teste1";
        String dbUser = "root";
        String dbPassword = "*Medeiros1";
        setupDatabase(dbUrl, dbUser, dbPassword);

        // Escolher entre cadastro ou reconhecimento
        String[] options = {"Cadastrar", "Reconhecer"};
        int choice = JOptionPane.showOptionDialog(null, "Escolha uma opção:", "Cadastro ou Reconhecimento",
                JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);

        if (choice == 0) {
            cadastrar(dbUrl, dbUser, dbPassword);
        } else if (choice == 1) {
            reconhecer(dbUrl, dbUser, dbPassword);
        }
    }

    private static void cadastrar(String dbUrl, String dbUser, String dbPassword) {
        String userName = JOptionPane.showInputDialog(null, "Digite seu nome para cadastro:");
        if (userName == null || userName.trim().isEmpty()) {
            System.out.println("Nome inválido. Saindo...");
            return;
        }

        VideoCapture camera = new VideoCapture(0);
        if (!camera.isOpened()) {
            System.out.println("Erro ao abrir a câmera.");
            return;
        }

        CascadeClassifier faceCascade = new CascadeClassifier("C:/OpenCV/sources/data/haarcascades/haarcascade_frontalface_alt.xml");

        Mat frame = new Mat();
        boolean faceCaptured = false;

        while (true) {
            if (camera.read(frame)) {
                Imgproc.cvtColor(frame, frame, Imgproc.COLOR_BGR2GRAY);

                MatOfRect faces = new MatOfRect();
                faceCascade.detectMultiScale(frame, faces, 1.1, 10, 0, new Size(30, 30), new Size());

                for (Rect face : faces.toArray()) {
                    Imgproc.rectangle(frame, face.tl(), face.br(), new Scalar(0, 255, 0), 2);

                    double faceHeight = face.height;
                    double faceWidth = face.width;
                    double faceArea = face.area();

                    double eyeDistance = calculateDistance(new Point(face.tl().x + face.width * 0.3, face.tl().y + face.height * 0.4),
                            new Point(face.tl().x + face.width * 0.7, face.tl().y + face.height * 0.4));
                    double foreheadToChin = face.height;
                    double cheekWidth = face.width;
                    double earToEar = face.width;
                    double lipWidth = calculateDistance(new Point(face.tl().x + face.width * 0.35, face.tl().y + face.height * 0.75),
                            new Point(face.tl().x + face.width * 0.65, face.tl().y + face.height * 0.75));

                    String faceHash = generateFaceHash(faceHeight, faceWidth, eyeDistance, foreheadToChin, cheekWidth, earToEar, lipWidth, faceArea);

                    saveFaceData(dbUrl, dbUser, dbPassword, userName, faceHeight, faceWidth, eyeDistance, foreheadToChin, cheekWidth, earToEar, lipWidth, faceArea, faceHash);
                    faceCaptured = true;
                    break;
                }

                HighGui.imshow("Cadastro Facial", frame);
                if (faceCaptured || HighGui.waitKey(1) == 27) {
                    break;
                }
            }
        }
        camera.release();
        System.out.println("Cadastro concluído!");
    }

    private static void reconhecer(String dbUrl, String dbUser, String dbPassword) {
        VideoCapture camera = new VideoCapture(0);
        if (!camera.isOpened()) {
            System.out.println("Erro ao abrir a câmera.");
            return;
        }

        CascadeClassifier faceCascade = new CascadeClassifier("C:/OpenCV/sources/data/haarcascades/haarcascade_frontalface_alt.xml");

        Mat frame = new Mat();
        boolean faceCaptured = false;

        while (true) {
            if (camera.read(frame)) {
                Imgproc.cvtColor(frame, frame, Imgproc.COLOR_BGR2GRAY);

                MatOfRect faces = new MatOfRect();
                faceCascade.detectMultiScale(frame, faces, 1.1, 10, 0, new Size(30, 30), new Size());

                for (Rect face : faces.toArray()) {
                    Imgproc.rectangle(frame, face.tl(), face.br(), new Scalar(0, 255, 0), 2);

                    double faceHeight = face.height;
                    double faceWidth = face.width;
                    double faceArea = face.area();

                    double eyeDistance = calculateDistance(new Point(face.tl().x + face.width * 0.3, face.tl().y + face.height * 0.4),
                            new Point(face.tl().x + face.width * 0.7, face.tl().y + face.height * 0.4));
                    double foreheadToChin = face.height;
                    double cheekWidth = face.width;
                    double earToEar = face.width;
                    double lipWidth = calculateDistance(new Point(face.tl().x + face.width * 0.35, face.tl().y + face.height * 0.75),
                            new Point(face.tl().x + face.width * 0.65, face.tl().y + face.height * 0.75));

                    String faceHash = generateFaceHash(faceHeight, faceWidth, eyeDistance, foreheadToChin, cheekWidth, earToEar, lipWidth, faceArea);

                    // Comparar hash gerado com hashes do banco de dados
                    String identifiedUser = findUserByHash(dbUrl, dbUser, dbPassword, faceHash);
                    if (identifiedUser != null) {
                        JOptionPane.showMessageDialog(null, "Usuário identificado: " + identifiedUser, "Reconhecimento Facial", JOptionPane.INFORMATION_MESSAGE);
                        System.out.println("Usuário identificado: " + identifiedUser);
                    } else {
                        JOptionPane.showMessageDialog(null, "Usuário não identificado.", "Reconhecimento Facial", JOptionPane.WARNING_MESSAGE);
                        System.out.println("Usuário não identificado.");
                    }
                    faceCaptured = true;
                    break;
                }

                HighGui.imshow("Reconhecimento Facial", frame);
                if (faceCaptured || HighGui.waitKey(1) == 27) {
                    break;
                }
            }
        }
        camera.release();
    }

    private static void setupDatabase(String dbUrl, String dbUser, String dbPassword) {
        try (Connection conn = DriverManager.getConnection(dbUrl, dbUser, dbPassword)) {
            String createTableSQL = """
                CREATE TABLE IF NOT EXISTS face_data (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    name VARCHAR(255) NOT NULL,
                    face_height DOUBLE,
                    face_width DOUBLE,
                    eye_distance DOUBLE,
                    forehead_to_chin DOUBLE,
                    cheek_width DOUBLE,
                    ear_to_ear DOUBLE,
                    lip_width DOUBLE,
                    face_area DOUBLE,
                    face_hash VARCHAR(255) NOT NULL
                );
            """;
            conn.createStatement().execute(createTableSQL);
        } catch (SQLException e) {
            System.err.println("Erro ao configurar o banco de dados: " + e.getMessage());
        }
    }

    private static void saveFaceData(String dbUrl, String dbUser, String dbPassword, String name, double faceHeight, double faceWidth, double eyeDistance, double foreheadToChin, double cheekWidth, double earToEar, double lipWidth, double faceArea, String faceHash) {
        String insertSQL = """
            INSERT INTO face_data (name, face_height, face_width, eye_distance, forehead_to_chin, cheek_width, ear_to_ear, lip_width, face_area, face_hash)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?);
        """;
        try (Connection conn = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
             PreparedStatement pstmt = conn.prepareStatement(insertSQL)) {
            pstmt.setString(1, name);
            pstmt.setDouble(2, faceHeight);
            pstmt.setDouble(3, faceWidth);
            pstmt.setDouble(4, eyeDistance);
            pstmt.setDouble(5, foreheadToChin);
            pstmt.setDouble(6, cheekWidth);
            pstmt.setDouble(7, earToEar);
            pstmt.setDouble(8, lipWidth);
            pstmt.setDouble(9, faceArea);
            pstmt.setString(10, faceHash);
            pstmt.executeUpdate();
            System.out.println("Dados faciais salvos para " + name);
        } catch (SQLException e) {
            System.err.println("Erro ao salvar dados faciais: " + e.getMessage());
        }
    }
    
        private static String findUserByHash(String dbUrl, String dbUser, String dbPassword, String faceHash) {
            String querySQL = "SELECT name FROM face_data WHERE face_hash = ?";
            try (Connection conn = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
                 PreparedStatement pstmt = conn.prepareStatement(querySQL)) {
                pstmt.setString(1, faceHash);
                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) {
                    return rs.getString("name");
                }
            } catch (SQLException e) {
                System.err.println("Erro ao buscar usuário pelo hash: " + e.getMessage());
            }
            return null;
        }
    
        private static String generateFaceHash(double... values) {
            try {
                MessageDigest md = MessageDigest.getInstance("SHA-256");
                StringBuilder sb = new StringBuilder();
                for (double value : values) {
                    sb.append(Double.toString(value));
                }
                byte[] hash = md.digest(sb.toString().getBytes());
                return Base64.getEncoder().encodeToString(hash);
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException("Erro ao gerar hash da impressão facial", e);
            }
        }
    
        private static double calculateDistance(Point p1, Point p2) {
            return Math.sqrt(Math.pow(p2.x - p1.x, 2) + Math.pow(p2.y - p1.y, 2));
        }
    }
    