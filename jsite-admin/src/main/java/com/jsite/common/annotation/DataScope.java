package com.jsite.common.annotation;

import java.lang.annotation.*;

/**
 * 数据权限注解
 *
 * @author jsite
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DataScope {

    /**
     * 部门表的别名
     */
    String deptAlias() default "d";

    /**
     * 用户表的别名
     */
    String userAlias() default "u";

    /**
     * 权限字符（用于多角色取并集数据权限）
     */
    String permission() default "";
}
