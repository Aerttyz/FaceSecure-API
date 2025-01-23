package com.face.secure.dtos;

import java.util.UUID;

import org.springframework.beans.BeanUtils;

import com.face.secure.model.UserModel;

public class UserDTO {

    private UUID id;

    private String name;

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

    public void setName(String name) {
        this.name = name;
    }

    public float[] getFaceEmbeddings() {
        return faceEmbeddings;
    }

    public void setFaceEmbeddings(float[] faceEmbeddings) {
        this.faceEmbeddings = faceEmbeddings;
    }

    public UserDTO(UserModel userModel) {
        BeanUtils.copyProperties(userModel, this);
    }
}
