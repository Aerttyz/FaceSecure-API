package com.face.secure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.opencv.core.Core;

@Configuration
public class AppConfig {

    @Bean
    public Core loadOpenCV() {

        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        return new Core();
    }
}
