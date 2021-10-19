package jdbc;

/**
 * 当查询单条记录时，却查出了多条记录结果，抛出该异常
 * selectMap("select * from t_car") ;
 */
public class RowCountException extends RuntimeException {
    public RowCountException(){
        super();
    }

    public RowCountException(String msg){
        super(msg) ;
    }
}
