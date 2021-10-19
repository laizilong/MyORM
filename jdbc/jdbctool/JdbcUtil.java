package jdbc.jdbctool;

import jdbc.exception.JdbcFormatException;
import jdbc.exception.RowCountException;
import jdbc.jdbctool.JdbcQuery;
import jdbc.jdbctool.JdbcUpdate;
import jdbc.jdbctool.Mapper;
import jdbc.jdbctool.ResultLoader;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * jdbc的工具，用来简化操作的
 */
public class JdbcUtil {
    private int executeUpdate(String sql, Object... params) {
        return (int) new JdbcUpdate().excute(sql, params);
    }

    private List<Map<String, Object>> executeQuery(String sql, Object... params) {
        return (List<Map<String, Object>>) new JdbcQuery().excute(sql, params);
    }

    public int insert(String sql, Object... params) {
        if (sql.substring(0, 6).equalsIgnoreCase("insert")) {
            return this.executeUpdate(sql, params);
        }
        throw new JdbcFormatException("not a insert statement:[" + sql + "]");
    }

    public int delete(String sql, Object... params) {
        if (sql.substring(0, 6).equalsIgnoreCase("delete")) {
            return this.executeUpdate(sql, params);
        }
        throw new JdbcFormatException("not a delete statement:[" + sql + "]");
    }

    public int update(String sql, Object... params) {
        if (sql.substring(0, 6).equalsIgnoreCase("update")) {
            return this.executeUpdate(sql, params);
        }
        throw new JdbcFormatException("not a update statement:[" + sql + "]");
    }

    public List<Map<String, Object>> selectListMap(String sql, Object... params) {
        if (sql.substring(0, 6).equalsIgnoreCase("select")) {
            return this.executeQuery(sql, params);
        }
        throw new JdbcFormatException("not a select statement:[" + sql + "]");
    }

    public Map<String, Object> selectMap(String sql, Object... params) {
        if (sql.substring(0, 6).equalsIgnoreCase("select")) {
            List<Map<String, Object>> rows = this.executeQuery(sql, params);
            if (rows != null && rows.size() == 1) {
                return rows.get(0);
            } else if (rows == null) {
                return null;
            } else {
                throw new RowCountException("neet one or null but  " + rows.size());
            }
        }
        throw new JdbcFormatException("not a select statement:[" + sql + "]");
    }

    //查询一个记录可以 组成任何对象
    public <T> List<T> selectList(String sql, jdbc.jdbctool.Mapper<T> mapper, Object... params) {
        List<Map<String, Object>> rows = this.selectListMap(sql, params);
        List<T> list = new ArrayList<T>();
        for (Map<String, Object> row : rows) {
            T t = mapper.orm(row);
            list.add(t);
        }
        return list;
    }

    public <T> T selectOne(String sql, Mapper<T> mapper, Object... params) {
        Map<String, Object> row = this.selectMap(sql, params);
        if (row == null)
            return null;
        return mapper.orm(row);
    }

    //查询一个记录可以 组成任何对象
    public <T> List<T> selectList(String sql, Class<T> type, Object... params) {
        try {
            List<Map<String, Object>> rows = this.selectListMap(sql, params);
            List<T> list = new ArrayList<T>();
            for (Map<String, Object> row : rows) {
                T t = ResultLoader.load(row, type);
                list.add(t);
            }
            return list;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
    public <T> T selectOne(String sql, Class<T> type, Object... params) {
        try {
            Map<String, Object> row = this.selectMap(sql, params);
            if (row == null)
                return null;
            return ResultLoader.load(row, type);
        }catch (Exception e){
            e.printStackTrace();
        }
       return null;
    }
}
