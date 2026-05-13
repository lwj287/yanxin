package com.yanxin.zhijian.service;

import java.io.File;
import java.io.FileInputStream;

public class DirectTestInference {
    public static void main(String[] args) throws Exception {
        YoloInferenceService service = new YoloInferenceService();
        service.initModel();
        
        File dir = new File("d:\\Code\\Trae\\yanxin\\backend\\zhijian\\uploads");
        File testImg = null;
        if (dir.exists()) {
            for (File d : dir.listFiles()) {
                if (d.isDirectory()) {
                    for (File f : d.listFiles()) {
                        if (f.getName().endsWith(".jpg") || f.getName().endsWith(".png")) {
                            testImg = f;
                            break;
                        }
                    }
                }
                if (testImg != null) break;
            }
        }
        
        if (testImg != null) {
            System.out.println("Using test image: " + testImg.getAbsolutePath());
            try (FileInputStream fis = new FileInputStream(testImg)) {
                String result = service.predictImage(fis);
                System.out.println("Result: " + result);
            }
        } else {
            System.out.println("No test image found.");
        }
        
        service.close();
    }
}
