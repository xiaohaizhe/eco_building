package com.giot.eco_building.aop;

import java.lang.annotation.*;

/**
 * @Author: pyt
 * @Date: 2020/6/15 14:41
 * @Description:
 */
@Target({ElementType.PARAMETER, ElementType.METHOD})//作用在参数和方法上
@Retention(RetentionPolicy.RUNTIME)//运行时注解
@Documented//表明这个注解应该被 javadoc工具记录
public @interface SystemControllerLog {
    String description() default "";
}
