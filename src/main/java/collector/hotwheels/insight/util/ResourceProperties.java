package collector.hotwheels.insight.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ResourceProperties {

    private static ResourceProperties instance;
    private static Properties properties;

    private ResourceProperties() {
        InputStream inputStream = null;

        properties = new Properties();
        try {
            inputStream = this.getClass()
                    .getClassLoader()
                    .getResourceAsStream("properties/insight-hotwheels-collector.properties");
            properties.load(inputStream);
        } catch (IOException e) {
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static ResourceProperties getInstance() {
        if (instance == null) {
            instance = new ResourceProperties();
        }
        return instance;
    }

    public String getProperty(String name) {
        String value = properties.getProperty(name);
        return value != null ? value : name;
    }
}
