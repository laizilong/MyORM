package pool;

import java.sql.*;

/**
 * 这个类是我们自己定义的
 * 用来描述的是   一个真实连接和一个状态的关系
 * 将一个真实可用连接和一个状态封装在一起
 * 形成一个新的对象MyConnection
 */
public class MyConnection extends AdapterConnection {

    private static String className;//配置文件  properties
    private static String url;
    private static String user;
    private static String password;

    //=============================================================
    //连接
    private Connection conn;
    //状态(false表示连接是空闲的--可用 true表示连接已经被他人占用--不可用)
    private boolean used = false;

    //一个静态块 目的是为了让加载类只执行一次
    static{
        try {
            className = ConfigReader.getPropertyValue("className");
            url = ConfigReader.getPropertyValue("url");
            user = ConfigReader.getPropertyValue("user");
            password = ConfigReader.getPropertyValue("password");

            Class.forName(className);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    //一个普通块 目的是为了给属性赋值{
    {
        try {
            conn = DriverManager.getConnection(url,user,password);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //属性对应的get/set方法
    public Connection getConn() {
        return conn;
    }
    public boolean isUsed() {//boolean的get方法  名字是is开头  规约
        return used;
    }
    public void setUsed(boolean used) {
        this.used = used;
    }

    //==============================================================================
    //==============================================================================
    //==============================================================================
    //==============================================================================
    //==============================================================================

    @Override
    public Statement createStatement() throws SQLException {
        return this.conn.createStatement();
    }

    @Override
    public PreparedStatement prepareStatement(String sql) throws SQLException {
        //需要创建一个pstat对象
        //Connection conn = DriverManager.getConnection("","","");//某一个子类
        //System.out.println(conn.getClass().getName());//JDBC4Connection
        //  多态的效果

        //  1.copy别人的---->所谓的"别人"是谁   Connection的某一个真实子类
        //      我们原来不知道那个类是谁
        //      今天知道了  JDBC4Connection
        //      这个类是通过导包来的   导入包中只有.class文件  粘贴不过来的
        //  2.继承你
        //      不行的  原来那个JDBC4Connection  没有修饰符  默认
        //      当前包内才允许继承  出了就不行了
        //  3.自己写
        //      还不会
        //      技术垄断了  只有原来那个真实的connection才会做这件事情
        PreparedStatement pstat = this.conn.prepareStatement(sql);
        return pstat;
    }

    @Override
    public void close() throws SQLException {
        this.used = false;//不真的关闭  释放连接状态
    }

}
