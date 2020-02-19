package collector.hotwheels.insight.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class I18FactoryFake {

    private static I18FactoryFake instance;
    private static Properties properties;

    private I18FactoryFake() {
        InputStream inputStream = null;

        properties = new Properties();
        try {
            inputStream = this.getClass()
                    .getClassLoader()
                    .getResourceAsStream("properties/i18n.properties");
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

    public static I18FactoryFake getInstance() {
        if (instance == null) {
            instance = new I18FactoryFake();
        }
        return instance;
    }

    public String getProperty(String name) {
        String value = properties.getProperty(name);
        return value != null ? value : name;
    }

}
