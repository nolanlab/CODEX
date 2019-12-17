package org.nolanlab.codex.upload.gui;

import java.io.IOException;
import java.util.Properties;

public class SystemInfo {
    private static Properties getProperties() throws IOException {
        Properties prop = new Properties();
        prop.load(SystemInfo.class.getClassLoader().getResourceAsStream("application.properties"));
        return prop;
    }

    public static String getAppName() {
        try {
            return getProperties().getProperty("name").trim();
        } catch (IOException e) {
            return "CODEX Uploader - Nolan lab";
        }
    }

    public static String getAppVersion() {
        try {
            return getProperties().getProperty("version").trim();
        } catch (IOException e) {
            return "0";
        }
    }
}
