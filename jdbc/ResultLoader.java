package jdbc;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;


public final class ResultLoader {
    private ResultLoader(){}

    /**
     * 将查询结果中的一条map记录，组成指定的对象类型
     */
    public static <T> T load(Map<String,Object> row,Class<T> type) throws IllegalAccessException, InstantiationException, InvocationTargetException {
        if(
                type == Integer.class || type == int.class
             || type == Long.class   || type == long.class
             || type == Double.class || type == double.class
             || type == String.class
        ){
            //只有1个字段属性
            for(Object o : row.values()){
                return (T)o;
            }
        }

        //证明不是组成一个简单的类型，那就是组成domain对象类型
        T obj = type.newInstance(); //new Car()

        Method[] methods = type.getMethods() ;
        for(Method method : methods){
            String mname = method.getName() ;
            if(mname.startsWith("set")){
                //证明是一个set方法，可以赋值   setCno()
                //通过set方法给属性赋值，属性值与数据库表对应
                String key = mname.substring(3).toUpperCase() ;//截掉set->Cno->CNO
                Object value = row.get(key);
                if(value == null){
                    //实体中有属性，但查询结果中没有，不需要赋值
                    continue ;
                }

                //当前对象属性，有对应的查询数据，需要通过当前set方法为属性赋值
                Class[] paramTypeArray = method.getParameterTypes() ;//获得set方法参数列表
                if(paramTypeArray.length != 1){
                    //不是一个参数的set方法，不符合set方法赋值要求
                    continue ;
                }

                Class paramType = paramTypeArray[0];
                if(paramType == Integer.class || paramType == int.class){
                    method.invoke(obj,(Integer)value) ;// car.setCno(value);
                }else if(paramType == Long.class || paramType == long.class){
                    method.invoke(obj,(Long)value) ;// car.setCno(value);
                }else if(paramType == Double.class || paramType == double.class){
                    method.invoke(obj,(Double)value) ;// car.setCno(value);
                }else if(paramType == String.class ){
                    method.invoke(obj,(String)value) ;// car.setCno(value);
                }else{
                    //其他类型暂时不考虑，但也不报错
                    continue ;
                }
            }
        }

        return obj ;
    }

}
