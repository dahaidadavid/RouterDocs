package com.dave.android.router_annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author rendawei
 * @date 2018/8/6
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.CLASS)
public @interface PageService {

    String path() default "";

    String desc() default "No desc.";

    String version() default "";

    String name() default "";
}
