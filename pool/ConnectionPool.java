package pool;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

/**
 * 这是我们设计的"连接池"类
 * 这个类需要将好多连接存储起来----属性 集合
 * 存储以后当然需要将连接获取到并给用户拿去使用----池子进行管理
 */
public class ConnectionPool {

    //设计一个单例设计模式
    //1.构造方法私有  从源头上截断对象的创建
    private ConnectionPool(){}
    //2.当前类的内部  创建唯一的一个对象  new ConnectionPool();
    //      属性  方法  构造方法(本身私有 也不行)  代码块(没有返回值 对象拿不走)
    //  私有的 静态的 当前类的对象作为属性
    private volatile static ConnectionPool connectionPool;
    //3.需要提供一个方式  让外面的人访问到我的属性对象
    //  公有的 静态的 方法 目的是为了返回那个唯一的对象
    public static ConnectionPool getInstance(){
        if(connectionPool==null){
            synchronized(ConnectionPool.class) {
                if(connectionPool==null) {
                    connectionPool = new ConnectionPool();//产生指令重排序的问题
                }
            }
        }
        return connectionPool;
    }


    //属性  最小连接个数
    private int minConnectCount = Integer.parseInt(ConfigReader.getPropertyValue("minConnectCount"));
    private int waitTime = Integer.parseInt(ConfigReader.getPropertyValue("waitTime"));

    //属性---大池子  list
    //Conn  MyConn
    private List<Connection> pool = new ArrayList();

    //一个普通块 大池子赋值
    {
        for(int i=1;i<=minConnectCount;i++){
            pool.add(new MyConnection());
        }
    }

    //方法---获取连接     返回值???  Conn  MyConn
    //从用户使用的角度来看 1和2都行
    //用户需要操作状态  用户用完了以后 不能关闭 需要切换状态---释放
    private Connection getMC(){
        Connection result = null;
        for(Connection conn : pool){
            MyConnection mc = (MyConnection)conn;
            if(mc.isUsed()==false){//这个连接是可用
                synchronized(mc) {
                    if(mc.isUsed()==false) {
                        mc.setUsed(true);//占为己有
                        result = mc;
                    }
                }
                break;
            }
        }
        return result;
    }


    //重新设计一个新的方法  目的是为了做一个用户等待的机制
    //最终目的还是为了获取一个连接
    public Connection getConnection(){
        Connection result = this.getMC();
        int count = 0;//记录循环的次数(次数刚好能计算出时间)
        while(result==null && count < waitTime*10){//太快啦
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            result = this.getMC();
            count++;
        }
        if(result==null){
            //超过5秒钟 但是还没有找到
            //自定义异常
            throw new SystemBusyException("系统繁忙 请稍后重试");
        }
        return result;
    }


}
