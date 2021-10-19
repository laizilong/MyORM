package jdbc.jdbctool;

import jdbc.jdbctool.JdbcTemplate;

import java.sql.SQLException;

/**
 * 按照jdbc的步骤来执行增删改
 */
public class JdbcUpdate extends JdbcTemplate {
    @Override
    public Object excuteSql() throws SQLException {
        return stmt.executeUpdate();
    }
}
