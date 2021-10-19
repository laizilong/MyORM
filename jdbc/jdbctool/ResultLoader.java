package jdbc.jdbctool;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
/**
 *将一条记录组装成一种对象类型
 */
public final class ResultLoader {
    public static  <T> T load(Map<String,Object> row,   Class<T> type) throws IllegalAccessException, InstantiationException, InvocationTargetException {
        if (type==Boolean.class||type==boolean.class||type==Float.class||type==float.class||type==Integer.class||type==int.class||type==Long.class||type==long.class||type==Double.class||type==double.class||type==String.class){
            for (Object o:row.values()){
                return (T) o;
            }
        }
        //证明组的不是一个基本类型，那就是组成domain的实体对象
        T o=type.newInstance();
        Method[] methods=type.getMethods();
        for (Method method:methods){
           String mname= method.getName();
           if (mname.startsWith("set")) {
               String key = mname.substring(3).toUpperCase();
               Object value = row.get(key);
               if (value == null) {
                   continue;
               }
               Class[] parameterTypes = method.getParameterTypes();
               if (parameterTypes.length != 1) {
                   continue;
               }
               Class parameterType = parameterTypes[0];
               if (parameterType == int.class || parameterType == Integer.class) {
                   method.invoke(o, (Integer)value);
               }else if (parameterType == long.class || parameterType == Long.class) {
                   method.invoke(o, (Long)value);
               }else if (parameterType == double.class || parameterType == Double.class) {
                   method.invoke(o, (Double)value);
               }else if (parameterType == float.class || parameterType == Float.class) {
                   method.invoke(o, (Float)value);
               }else if (parameterType == String.class ){
                   method.invoke(o, (String)value);
               }else {
                   continue;
               }
           }
        }
        return o;
    }
}
