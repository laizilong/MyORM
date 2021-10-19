package jdbc.jdbctool;

import jdbc.jdbctool.JdbcTemplate;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 按照jdbc的步骤来执行查询操作
 */
public class JdbcQuery extends JdbcTemplate {
    @Override
    public Object excuteSql() throws SQLException {//执行完这个就要被关闭了，所以结果集不适合返回出去
        ResultSet rs = stmt.executeQuery();
        List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
        while (rs.next()) {
            Map<String, Object> map = new HashMap<String, Object>();
            for (int i = 1; i <=rs.getMetaData().getColumnCount(); i++) {
                String key = rs.getMetaData().getColumnName(i);
                Object value = rs.getObject(i);
                map.put(key.toUpperCase(), value);
            }
                result.add(map);
        }
        return result;
    }
}
