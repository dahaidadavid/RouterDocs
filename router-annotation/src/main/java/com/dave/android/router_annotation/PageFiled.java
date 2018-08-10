package com.dave.android.router_annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author rendawei
 * @date 2018/8/6
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.CLASS)
public @interface PageFiled {

    // Mark param's name or service name.
    String name() default "";

    // If required, app will be crash when value is null.
    // Primitive type wont be check!
    boolean required() default false;

    // Description of the field
    String desc() default "No desc.";

    String version() default "";
}
