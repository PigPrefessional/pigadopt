package com.ai2020lab.pigadopted;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by Rocky on 16/4/13.
 */
public class TestHelper {

    public static String getStringFromFile(String filePath) {
        StringBuilder sb = new StringBuilder();

        String workPath = System.getProperty("user.dir");
        String absPath =  workPath + File.separator + filePath;

        try {
            InputStreamReader reader = new InputStreamReader(new FileInputStream(new File(absPath)));

            BufferedReader bufReader = new BufferedReader(reader);
            String line = "";

            while ((line = bufReader.readLine()) != null)
                sb.append(line);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return sb.toString();
    }
}
