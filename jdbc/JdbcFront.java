package jdbc;

import jdbc.annotations.Delete;
import jdbc.annotations.Insert;
import jdbc.annotations.Update;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * jdbc交互时前端操作对象
 * 主要用来处理一些jdbc之前做的是
 * 解析sql中的#{}部分，与对象参数匹配
 * String sql = "insert into t_car values(#{cno},#{cname},#{color},#{price})";
 * util.insert(sql,car);
 *
 * ---> 转换成
 *
 * String sql = "insert into t_car values(?,?,?,?)";
 * JdbcUtil util = new JdbcUtil();
 * 	util.insert(sql,car.getCno(),car.getCname(),car.getColor(),car.getPrice());
 */
public class JdbcFront {

    public int insert(String sql , Object param) {
        try {
            SqlParseObject o = this.parseSql(sql,param);
            JdbcUtil util = new JdbcUtil();
            return util.insert(o.sql,o.values);
        }catch(Exception e){
            e.printStackTrace();
        }
        return 0 ;
    }

    public int update(String sql , Object param) {
        try {
            SqlParseObject o = this.parseSql(sql,param);
            JdbcUtil util = new JdbcUtil();
            return util.update(o.sql,o.values);
        }catch(Exception e){
            e.printStackTrace();
        }
        return 0 ;
    }

    public int delete(String sql , Object param) {
        try {
            SqlParseObject o = this.parseSql(sql,param);
            JdbcUtil util = new JdbcUtil();
            return util.delete(o.sql,o.values);
        }catch(Exception e){
            e.printStackTrace();
        }
        return 0 ;
    }


    public <T> List<T> selectList(String sql ,Class<T> type , Object param) {
        try {
            SqlParseObject o = this.parseSql(sql,param);
            JdbcUtil util = new JdbcUtil();
            return util.selectList(o.sql,type,o.values);
        }catch(Exception e){
            e.printStackTrace();
        }
        return null ;
    }

    public <T> T selectOne(String sql ,Class<T> type , Object param) {
        try {
            SqlParseObject o = this.parseSql(sql,param);
            JdbcUtil util = new JdbcUtil();
            return util.selectOne(o.sql,type,o.values);
        }catch(Exception e){
            e.printStackTrace();
        }
        return null ;
    }
    // -----------------------------------------------------------------------------
    /**
        创建dao接口的实现类（代理）
     */
    private Class interfaceType ;
    public <T> T createDaoImpl(Class<T> interfaceType){
        this.interfaceType = interfaceType;
        return (T) Proxy.newProxyInstance(
                interfaceType.getClassLoader(),
                new Class[]{interfaceType},
                new DaoInvocationHandler()
        );
    }

    /**
     * 定义dao代理要如何实现功能，策略
     */
    class DaoInvocationHandler implements InvocationHandler{

        /*
        proxy 当前代理对象，不要操作，会出现死循环
        method 代理当前要执行的接口中继承方法,可以获得sql和返回类型信息
        args  调用方法时传递的参数
         */
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            //获得代理对象后，根据接口方法，调用代理对象的方式时，就会执行当前的方法。
                        /*
                        Car car= new Car(7,"qq","red",50000);
                        JdbcFront front = new JdbcFront();
                        ICarDao dao = front.createDaoImpl(ICarDao.class) ;
                        dao.save(car) -->> invoke()

                          根据调用方法，执行相应的jdbc操作
                          执行jdbc操作时，需要sql，需要参数，需要返回类型
                            sql 可以通过接口方法上的注解获得
                            返回类型 可以通过接口方法的返回类型
                            参数  invoke方法中的args就是参数
                         */
             method.getName() ;//save
            interfaceType.getName();//com.dao.ICarDao.save


            Annotation a = method.getAnnotations()[0];//获得接口方法上的注解
            Method m =a.getClass().getMethod("value") ;//获得注解中value属性方法
            String sql = (String) m.invoke(a);
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
    }

//----------------------------------------------------------------------------------

    private SqlParseObject parseSql(String sql,Object param)throws Exception{
        //装载sql语句中#{}指定属性key
        List<String> paramNameArray = new ArrayList<String>();

        //遍历sql中的#{}
        while (true) {
            int i1 = sql.indexOf("#{");
            int i2 = sql.indexOf("}");
            if (i1 != -1 && i2 != -1 && i1 < i2) {
                //发现了一个#{}
                String key = sql.substring(i1 + 2, i2).trim();
                paramNameArray.add(key);
                //将当前的#{} 替换成 ?
                sql = sql.substring(0, i1) + "?" + sql.substring(i2 + 1);
            } else {
                //没有#{}
                break;
            }
        }
            /*
                根据sql中指定的参数顺序
                从传递的参数对象中获取对应的数值
                并按照顺序组成数组

                经分析参数可能会有3种情况
                    * 简单参数  int,double,string
                    * map
                    * domain
            * */
        List<Object> values = new ArrayList<Object>();//装参数值
        if(param != null) {
            Class paramType = param.getClass();
            if (paramType == Integer.class || paramType == int.class
                    || paramType == Double.class || paramType == double.class
                    || paramType == String.class
            ) {
                //是简单的类型，只会有1个值
                //此时sql中的参数应该也是一个
                values.add(param);
            } else if (paramType == Map.class || paramType == HashMap.class) {
                for (String paramName : paramNameArray) {
                    //sql中参数名就是map中的key
                    Map map = (Map) param;
                    Object value = map.get(paramName);
                    values.add(value);
                }
            } else {
                //domain对象
                for (String paramName : paramNameArray) {
                    //sql中参数名就是domain对象中的属性名->get方法
                    //cno -> getCno
                    //a->getA
                    String mname = "get" + paramName.substring(0, 1).toUpperCase() + paramName.substring(1);
                    Method method = paramType.getMethod(mname);// getCno()
                    Object value = method.invoke(param);
                    values.add(value);
                }
            }
        }

        return new  SqlParseObject(sql,values.toArray());
    }


    private class SqlParseObject{
        String sql ;//装载解析后，将#变成?的sql
        Object[] values ;//装载与sql中#指定的参数相同顺序的参数值

        public SqlParseObject(String sql, Object[] values) {
            this.sql = sql;
            this.values = values;
        }
    }

}
