package jdbc.exception;

public class JdbcFormatException extends RuntimeException {
    public JdbcFormatException() {
        super();
    }
    public JdbcFormatException(String message) {
        super(message);
    }
}
