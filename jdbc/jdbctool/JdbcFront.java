package jdbc.jdbctool;

import jdbc.annontetion.Delete;
import jdbc.annontetion.Insert;
import jdbc.annontetion.Update;
import jdbc.jdbctool.JdbcUtil;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**jdbc交互时的提前处理
 * 是对sql语句参数的优化
 * 处理jdbc的前端
 * 主要作用：
 * String sql="select * from t_car values(#{cno},#{},#{},#{})";
 *    new JdbcFront().insert(sql,car);
 * 转化为-->String sql = "insert into car values(?,?,?,?)";
 *    util.insert(sql, new Object[]{car.getCno(), car.getCname(), car.getColor(), car.getPrice()});
 *    实现了更灵活的参数传递
 */
@SuppressWarnings("all")

public class JdbcFront {
    public int insert(String sql, Object param) {
        try {
            //装载sql语句的集合
            ParseSqlObject o = this.parseSqL(sql, param);
            jdbc.jdbctool.JdbcUtil util = new jdbc.jdbctool.JdbcUtil();
            return util.insert(o.sql, o.values);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int update(String sql, Object param) {
        try {
            //装载sql语句的集合
            ParseSqlObject o = this.parseSqL(sql, param);
            jdbc.jdbctool.JdbcUtil util = new jdbc.jdbctool.JdbcUtil();
            return util.update(o.sql, o.values);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int delete(String sql, Object param) {
        try {
            //装载sql语句的集合
            ParseSqlObject o = this.parseSqL(sql, param);
            jdbc.jdbctool.JdbcUtil util = new jdbc.jdbctool.JdbcUtil();
            return util.delete(o.sql, o.values);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public <T> List<T> selectList(String sql, Class<T> type, Object param) {
        try {
            //装载sql语句的集合
            ParseSqlObject o = this.parseSqL(sql, param);
           jdbc.jdbctool.JdbcUtil util = new jdbc.jdbctool.JdbcUtil();
            return util.selectList(o.sql, type, o.values);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public <T> T selectOne(String sql, Class<T> type, Object param) {
        try {
            //装载sql语句的集合
            ParseSqlObject o = this.parseSqL(sql, param);
            jdbc.jdbctool.JdbcUtil util = new JdbcUtil();
            return util.selectOne(o.sql, type, o.values);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    //---------------------------------------------------------------------------------------------------------

    /**
     *创建dao接口的实现类（代理）
     */
    public <T> T createDaoImpl(Class<T> interfaceType){
return (T) Proxy.newProxyInstance(interfaceType.getClassLoader(), new Class[]{interfaceType}, new InvocationHandler() {
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Annotation a=method.getAnnotations()[0];
       Method m= a.getClass().getMethod("value");
       String sql=(String) m.invoke(a);
        Object param = args==null?null:args[0];

        Object returnValue = null ;
        if(a.annotationType() == Insert.class){
            returnValue =insert(sql,param);
        }else if(a.annotationType() == Update.class){
            returnValue =update(sql,param);
        }else if(a.annotationType() == Delete.class){
            returnValue =delete(sql,param) ;
        }else{
            //查询操作
            //需要考虑是集合查询还是单记录查询，与返回类型有关
            Class rt = method.getReturnType() ; //List
            if(rt == List.class){
                //返回类型是List集合，查询多条记录，集合的泛型是结果类型
                //如何获得泛型
                Type type = method.getGenericReturnType() ;//获得完整返回类型List<Car>
                ParameterizedType pt = (ParameterizedType)type ;
                Class fx = (Class)pt.getActualTypeArguments()[0] ;//当前的返回类型中只有一个泛型
                returnValue =selectList(sql,fx ,param);
            }else{
                //返回类型是domain，查询一条，返回类型即查询结果类型
                returnValue =selectOne(sql,rt,param);
            }
        }
        return returnValue;
    }
});
    }


    public ParseSqlObject parseSqL(String sql, Object param) throws Exception {
        List<String> parmeNameArray = new ArrayList<String>();
        while (true) {
            int i1 = sql.indexOf("#{");
            int i2 = sql.indexOf("}");
            if (i1 != -1 && i2 != -1 && i1 < i2) {
                String key = sql.substring(i1 + 2, i2).trim();
                parmeNameArray.add(key);
                sql = sql.substring(0, i1) + "?" + sql.substring(i2 + 1);
            } else {
                break;
            }
        }
        System.out.println(sql);
        System.out.println(parmeNameArray);
        List<Object> values = new ArrayList<Object>();
        Class parmesType = param.getClass();
        if (parmesType == Float.class || parmesType == float.class || parmesType == Integer.class || parmesType == int.class || parmesType == Long.class || parmesType == long.class || parmesType == Double.class || parmesType == double.class || parmesType == String.class) {
            values.add(param);
        } else if (parmesType == Map.class||parmesType== HashMap.class) {
            for (String parmeName : parmeNameArray) {
                Map map = (Map) param;
                Object value = map.get(parmeName);
                values.add(value);
            }
        } else {
            //domain对象
            for (String parmeName : parmeNameArray) {
                String mname = "get" + parmeName.substring(0, 1).toUpperCase() + parmeName.substring(1);
                Method method = parmesType.getMethod(mname);
                Object value = method.invoke(param);
                values.add(value);
            }
        }
        return new ParseSqlObject(sql, values.toArray());
    }

    private class ParseSqlObject {
        String sql;
        Object[] values;

        public ParseSqlObject() {
        }
        public ParseSqlObject(String sql, Object[] values) {
            this.sql = sql;
            this.values = values;
        }
    }
}
