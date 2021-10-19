package jdbc.jdbctool;

import pool.ConnectionPool;

import java.sql.*;

/**
 * jdbc模版，负责指定jdbc执行步骤
 * 1.步骤是创建连接，
 * 2.创建状态参数
 * 3.执行状态参数
 * 4.关闭结果集和连接
 * 用到了模版模式
 */
public abstract class JdbcTemplate {
    protected Connection conn;
    protected PreparedStatement stmt;//
    protected ResultSet rs;

    public Object excute(String sql, Object... params) {
        try {
            //1
            createConnection();
            //2
            createStatmenet(sql, params);
            //3
            Object result = excuteSql();
            //4
            close();
            return result;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return null;
    }

    public void createConnection() {
        ConnectionPool pool = ConnectionPool.getInstance();
        conn = pool.getConnection();
    }

    public void createStatmenet(String sql, Object... params) throws SQLException {//预状态参数可能有参数，所以因该要用到动态
        stmt = conn.prepareStatement(sql);
        for (int i = 0; i < params.length; i++) {
            ((PreparedStatement) stmt).setObject(i + 1, params[i]);
        }
    }

    /**
     * 执行更删改操作 会返回 int
     * 执行查询操作 可能会返回各种类型
     * 所以返回值用了Object
     *
     * @return
     */
    public abstract Object excuteSql() throws SQLException;

    public void close() throws SQLException {
//可能没有结果集
        if (rs != null) {
            rs.close();
        }
        if (stmt != null) {
            stmt.close();
        }
        if (conn != null) {
            conn.close();
        }
    }
}
