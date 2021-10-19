package pool;

import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * 这个类的目的是为了在启动程序时候
 * 一次性读取properties配置文件的信息
 * 这些信息存在一个map缓存中
 * 为了创建连接和连接池管理而使用
 */
public class ConfigReader {

    private static Properties properties;
    private static Map<String,String> configMap;

    static{
        properties = new Properties();
        configMap = new HashMap();

        try {
            //按照原来new FileReader("src/configuration.properties");
            //现在执行的项目是一个JavaProject 不需要服务器支持  main方法
            //未来执行的项目是一个WEBProject  需要Tomcat这种WEB容器的支持
            //  写好的所有Java类当做请求的资源统一交给WEB容器来进行管理
            //  通过浏览器跟WEB容器发送请求  请求容器帮我们找到资源  执行资源  给予我们响应
            //  相对路径发生了改变(原来是我们自己工程下的src  以后是WEB容器下的某一个人家规定的路径)
            //  path
            //  classPath--->告知 .class文件在哪里的  告诉给ClassLoader
            InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("configuration.properties");
            properties.load(inputStream);
            //将文件中所有内容都读出来 进行遍历
            //properties文件的结构是一个map集合结构

            //获取全部的key
            Enumeration en = properties.propertyNames();//Set = map.keySet();
            while(en.hasMoreElements()){//set--Iteration  it.hasNext();
                String key = (String)en.nextElement();//    it.next();
                String value = properties.getProperty(key);//   value = map.get(key);
                configMap.put(key,value);
            }

            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    //给使用者提供一个获取value的方法
    public static String getPropertyValue(String key){
        return configMap.get(key);
    }
}
