package com.face.secure.util;

import org.opencv.core.Core;


public class OpenCvLoader {

    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

    
    public static void init() {
        if(!Core.NATIVE_LIBRARY_NAME.isEmpty()){
            System.out.println("OpenCV loaded successfully");
        } else {
            System.out.println("OpenCV not loaded");
        }
    }
}
