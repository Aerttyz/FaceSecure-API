package com.face.secure.dtos;

import org.springframework.beans.BeanUtils;

import com.face.secure.model.UserModel;

public class UserDTO {

    private long id;

    private String name;

    private float[] faceEmbeddings;

    public long getId() {
        return id;
    }

    public void setId(long id) {
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
