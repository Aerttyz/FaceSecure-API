package com.face.secure.service;

import java.io.File;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.face.secure.model.UserModel;
import com.face.secure.repositories.UserRepository;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public void register(UserModel userModel) {
        int label = getUnicLabel();

        userModel.setLabel(label);
        userModel.setName(userModel.getName());
        userRepository.save(userModel);

        File directory  = new File("E:/dataset/" + label);
        if(!directory.exists()) {
            directory.mkdirs();
        }
    }

    public int getUnicLabel() {
        File dataset = new File("E:/dataset");
        File[] labelDirs  = dataset.listFiles(File::isDirectory);

        int max = 0;
        for(File labelDir : labelDirs) {
            int label = Integer.parseInt(labelDir.getName());
            if(label > max) {
                max = label;
            }
        }
        return max + 1;
    }

    public String getNameByLabel(int label){
        return userRepository.findByLabel(label).map(UserModel::getName).orElse("Desconhecido");
    }
}
