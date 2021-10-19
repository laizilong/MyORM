package pool;

import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Properties;

public class LoderFile {
    private static Properties properties;
    private static HashMap<String, String> map;

    static {
        properties = new Properties();
        map = new HashMap<String, String>();
        FileReader fileReader=null;
//        InputStream intputStream = null;
        try {
            properties = new Properties();
            fileReader = new FileReader("src//pool//configuration.properties");
//            intputStream= Thread.currentThread().getContextClassLoader().getResourceAsStream("configuration.properties");
            properties.load(fileReader);
            Enumeration enumeration = properties.propertyNames();
            while (enumeration.hasMoreElements()) {
                String key = (String) enumeration.nextElement();
                String vlaue = properties.getProperty(key);
                map.put(key, vlaue);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fileReader != null) {
                    fileReader.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static String getPropertyValue(String key) {
        return map.get(key);
    }
}
