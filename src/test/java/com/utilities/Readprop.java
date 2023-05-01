package com.utilities;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public class Readprop {

    public static String getData(String data) {
        try {
            FileInputStream fis = new FileInputStream("src/test/resources/config.properties");
            Properties ps = new Properties();
            ps.load(fis);
            String output = ps.getProperty(data);
            return output;
        }
        catch (Exception e) {
            return e.toString();
        }
    }
    }

