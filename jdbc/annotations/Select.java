package jdbc.annotations;

import java.lang.annotation.*;

@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD) //注解在方法上使用
public @interface Select {
    String value() ;
}
