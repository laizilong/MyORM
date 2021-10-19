package jdbc.annotations;

import java.lang.annotation.*;

@Inherited
@Retention(RetentionPolicy.RUNTIME) //注解可以在反射中使用
@Target(ElementType.METHOD) //注解在方法上使用
public @interface Insert {
    String value() ;
}
