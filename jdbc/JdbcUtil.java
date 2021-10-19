package jdbc;

import pool.ConnectionPool;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * jdbc工具，专门负责实现jdbc交互过程
 */
public class JdbcUtil {
    //实现增删改
    private int executeUpdate(String sql , Object...params){
        return (int) new JdbcUpdate().execute(sql,params);
    }

    //实现查询
    private List<Map<String,Object>> executeQuery(String sql , Object...params){
        return (List<Map<String, Object>>) new JdbcQuery().execute(sql,params);
    }


    public int insert(String sql , Object...params){
        if(sql.substring(0,6).equalsIgnoreCase("insert")){
            return this.executeUpdate(sql,params) ;
        }
        //语句不对等。出错了。
        throw new SqlFormatException("not a insert statement : ["+sql+"]");
    }

    public int delete(String sql , Object...params){
        if(sql.substring(0,6).equalsIgnoreCase("delete")){
            return this.executeUpdate(sql,params) ;
        }
        //语句不对等。出错了。
        throw new SqlFormatException("not a delete statement : ["+sql+"]");
    }

    public int update(String sql , Object...params){
        if(sql.substring(0,6).equalsIgnoreCase("update")){
            return this.executeUpdate(sql,params) ;
        }
        //语句不对等。出错了。
        throw new SqlFormatException("not a update statement : ["+sql+"]");
    }

    /**
     * 查询多条记录
     * @param sql
     * @param params
     * @return
     */
    public List<Map<String,Object>> selectListMap(String sql,Object...params){
        if(sql.substring(0,6).equalsIgnoreCase("select")){
            return this.executeQuery(sql,params) ;
        }
        //语句不对等。出错了。
        throw new SqlFormatException("not a select statement : ["+sql+"]");
    }

    /**
     * 查询单条记录
     * @param sql
     * @param params
     * @return
     */
    public Map<String,Object> selectMap(String sql,Object...params){
        if(sql.substring(0,6).equalsIgnoreCase("select")){
            List<Map<String,Object>> rows =  this.executeQuery(sql,params) ;
            if(rows != null && rows.size() == 1){
                return rows.get(0) ;
            }else if (rows == null || rows.size() == 0){
                return null ;
            }else{
                throw new RowCountException("neet one or null but return "+rows.size()) ;
            }
        }
        //语句不对等。出错了。
        throw new SqlFormatException("not a select statement : ["+sql+"]");
    }

    /*
        查询所有的记录，可以组成具体的对象
     */
    public <T> List<T> selectList(String sql,Mapper<T> mapper,Object...params){
        List<Map<String,Object>> rows = this.selectListMap(sql,params);
        List<T> ts = new ArrayList<T>();
        for(Map<String,Object> row : rows){
            T t = mapper.orm(row) ;
            ts.add(t) ;
        }
        return ts ;
    }

    public <T> T selectOne(String sql,Mapper<T> mapper,Object...params){
        Map<String,Object> row = this.selectMap(sql,params);
        if(row == null)
            return null ;
        return mapper.orm(row) ;
    }


    /**
       查询所有的记录，可以组成具体的对象
       Class<T> type 指定组装的具体类型
        * 通过反射组装对象
    */
    public <T> List<T> selectList(String sql,Class<T> type,Object...params){
        try {
            List<Map<String, Object>> rows = this.selectListMap(sql, params);
            List<T> ts = new ArrayList<T>();
            for (Map<String, Object> row : rows) {
                //自己想办法组装，反射
                T t = ResultLoader.load(row, type);
                ts.add(t);
            }
            return ts;
        }catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public <T> T selectOne(String sql,Class<T> type,Object...params){
        try {
            Map<String,Object> row = this.selectMap(sql,params);
            if(row == null)
                return null ;
            return ResultLoader.load(row,type) ;
        }catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }

}
