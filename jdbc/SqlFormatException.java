package jdbc;

/**
 * 当执行的sql语句 与 调用的代码不匹配时，抛出该异常
 * insert("delete..")
 */
public class SqlFormatException extends RuntimeException{
    public SqlFormatException(){
        super();
    }

    public SqlFormatException(String msg){
        super(msg) ;
    }
}
