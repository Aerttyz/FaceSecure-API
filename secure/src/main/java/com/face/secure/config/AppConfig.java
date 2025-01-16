package com.face.secure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.opencv.core.Core;

@Configuration
public class AppConfig {

    // Este bean carrega a biblioteca nativa do OpenCV na inicialização da aplicação
    @Bean
    public Core loadOpenCV() {
        // Carrega a biblioteca nativa do OpenCV
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        return new Core();
    }
}
