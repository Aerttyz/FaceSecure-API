package com.face.secure.model;

import java.util.UUID;



import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;

@Entity
@Table(name = "users")
public class UserModel {
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private UUID id;
    private String name;
    
    @Lob
    @Column(name = "face_embeddings")
    private float[] faceEmbeddings;
    
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public float[] getFaceEmbeddings() {
        return faceEmbeddings;
    }

    public void setFaceEmbeddings(float[] faceEmbeddings) {
        this.faceEmbeddings = faceEmbeddings;
    }

    public void setName(String name) {
        this.name = name;
    }
}
