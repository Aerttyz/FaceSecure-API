package com.face.secure;

import org.opencv.core.Core;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class FaceSecureApplication {

	public static void main(String[] args) {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		System.out.println("OpenCV loaded successfully" + Core.VERSION);
		SpringApplication.run(FaceSecureApplication.class, args);
	}

}
