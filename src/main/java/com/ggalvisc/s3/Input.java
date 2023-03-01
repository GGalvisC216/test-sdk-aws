package com.ggalvisc.s3;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Input {

    public String getBucketName() throws Exception {
        return readProperties().getProperty("bucket_name");
    }

    public String getRegion() throws Exception {
        return readProperties().getProperty("region");
    }

    public String getFile() throws Exception {
        return readProperties().getProperty("file");
    }

    public String getObjectName() throws Exception {
        return readProperties().getProperty("object_name");
    }

    public String getNewObjectName() throws Exception {
        return readProperties().getProperty("new_object_name");
    }

    public static Properties readProperties() throws Exception {
        InputStream configFile = Input.class.getClassLoader().getResourceAsStream("config.properties");
        Properties properties = new Properties();
        try {
            properties.load(configFile);
        } catch (FileNotFoundException fnfe) {
            fnfe.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        return properties;
    }
}
