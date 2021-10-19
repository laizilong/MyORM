package jdbc.jdbctool;

import java.util.Map;

public interface Mapper<T> {
    public T orm(Map<String,Object> row);
}
