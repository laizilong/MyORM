package jdbc;

import java.util.Map;

/**
 * 设置表数据组成具体对象的规则
 * 如果框架的使用者（dao）希望查询结果组成具体的对象
 *  那么怎么组装，由使用者提供
 *  框架只负责按照要求组装
 * 如何保证使用者提出的要求符合规范呢
 * 只需要符合当前接口规范即可
 */
public interface Mapper<T> {
    /**
    将一条表记录组成指定的对象
     */
    public T orm(Map<String, Object> row) ;

}
