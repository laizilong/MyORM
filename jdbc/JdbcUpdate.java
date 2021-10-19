package jdbc;

import java.sql.SQLException;

/**
 * 按照jdbc步骤，执行增删改操作
 */
public class JdbcUpdate extends JdbcTemplate {

    protected Object executeSql() throws SQLException {
        return stmt.executeUpdate();
    }
}
