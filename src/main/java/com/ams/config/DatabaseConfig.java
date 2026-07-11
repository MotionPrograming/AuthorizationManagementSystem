package com.ams.config;

import java.io.InputStream;
import java.util.Properties;

public final class DatabaseConfig {

    private static final Properties properties = new Properties();


    static {

        try (InputStream input =
             DatabaseConfig.class
             .getClassLoader()
             .getResourceAsStream("db.properties")) {


            if(input == null){
                throw new RuntimeException(
                    "db.properties not found"
                );
            }

            properties.load(input);


        } catch(Exception e){

            throw new RuntimeException(
                "Database configuration loading failed",
                e
            );
        }
    }



    private DatabaseConfig(){}



    public static String getProperty(String key){

        String value = properties.getProperty(key);

        if(value == null){

            throw new RuntimeException(
                "Missing database property: " + key
            );
        }

        return value;
    }
}