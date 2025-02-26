package winter.annotation.methodlevel;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import winter.data.enumdata.RequestParamType;

import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.ElementType;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface RequestParam {
    String name() default "";

    RequestParamType type() default RequestParamType.TEXT;

    boolean required() default false;
}
