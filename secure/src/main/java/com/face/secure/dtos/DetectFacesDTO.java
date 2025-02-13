package com.face.secure.dtos;

public class DetectFacesDTO {
    private String name;

    private String confidence;

    private boolean faceDetected;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getConfidence() {
        return confidence;
    }

    public void setConfidence(String confidence) {
        this.confidence = confidence;
    }

    public boolean isFaceDetected() {
        return faceDetected;
    }

    public void setFaceDetected(boolean faceDetected) {
        this.faceDetected = faceDetected;
    }

    public DetectFacesDTO() {
    }

    public DetectFacesDTO(String name, String confidence, boolean faceDetected) {
        this.name = name;
        this.confidence = confidence;
        this.faceDetected = faceDetected;
    }

    
}
