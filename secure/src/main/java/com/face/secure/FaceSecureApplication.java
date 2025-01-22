package com.face.secure;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.lang.reflect.Field;

@SpringBootApplication
public class FaceSecureApplication {

    public static void main(String[] args) {
        // Definindo o caminho para as bibliotecas nativas do OpenCV
        // System.setProperty("java.library.path", "C:\\Users\\raiim\\Downloads\\opencv\\build\\java\\x64");

        // // Atualizando a propriedade do java.library.path
        // try {
        //     Field fieldSysPath = ClassLoader.class.getDeclaredField("sys_paths");
        //     fieldSysPath.setAccessible(true);
        //     fieldSysPath.set(null, null); // Força a JVM a recarregar a propriedade
        // } catch (Exception e) {
        //     e.printStackTrace();
        // }

        // Inicia o Spring Boot
        SpringApplication.run(FaceSecureApplication.class, args);
    }
}
