package jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 按照jdbc步骤，执行查询操作
 */
public class JdbcQuery extends JdbcTemplate {

    @Override
    protected Object executeSql() throws SQLException {
        ResultSet rs = stmt.executeQuery();
        //将表结构中的数据 转存到 List<Map>集合中
        /**
         * 表中的所有记录，最终会组成List集合
         * 表中的每条记录，会组成Map集合
         */
        List<Map<String,Object>> results = new ArrayList<Map<String,Object>>();
        while(rs.next()){
            //每次获取一条记录,准备装载到一个Map中
            Map<String,Object> map = new HashMap<String,Object>();
            //循环当前这条记录所有的属性和属性值
            for(int i=1;i<=rs.getMetaData().getColumnCount();i++){
                String key = rs.getMetaData().getColumnName(i) ;//获取当前属性名
                Object value = rs.getObject(i);
                map.put(key.toUpperCase(),value);
            }
            results.add(map);
        }
        return results;
    }
}
