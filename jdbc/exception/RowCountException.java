package jdbc.exception;

/**
 * 当查询单条记录时，却查出了多条记录，抛出异常
 */
public class RowCountException extends RuntimeException {
    public RowCountException(){
        super();
    }

    public RowCountException(String message) {
        super(message);
    }
}
