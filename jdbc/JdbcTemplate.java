package jdbc;

import pool.ConnectionPool;

import java.sql.*;

/**
 * jdbc模板，负责指定jdbc执行步骤
 */
public abstract class JdbcTemplate {

    protected Connection conn ;
    protected PreparedStatement stmt ;
    protected ResultSet rs ;

    public Object execute(String sql,Object...params){
        try {
            //获取连接
            createConnection();
            //创建命令集
            createStatmenet(sql, params);
            //执行sql
            Object result = executeSql();
            //各种关闭
            close();

            return result ;
        }catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }

    private void createConnection(){
        ConnectionPool pool = ConnectionPool.getInstance();
        conn  = pool.getConnection() ;
    }

    private void createStatmenet(String sql,Object...params) throws SQLException {
        stmt = conn.prepareStatement(sql);
        //理论上，params数组中有几个数据，就证明sql中有几个?
        for(int i=0;i<params.length;i++){
            ((PreparedStatement) stmt).setObject(i+1,params[i]);
        }
    }

    /**
     * 增删改 返回 int  表示增删改记录的行数
     * 查询   返回 查询结果。 各种类型
     * @return
     */
    protected abstract Object executeSql() throws SQLException;

    private void close() throws SQLException {
        if(rs != null){
            rs.close();
        }
        if(stmt!= null)
            stmt.close();
        if(conn != null){
            conn.close();
        }
    }

}
