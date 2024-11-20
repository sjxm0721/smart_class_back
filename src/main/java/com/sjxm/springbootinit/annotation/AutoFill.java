package com.sjxm.springbootinit.annotation;


import com.sjxm.springbootinit.model.enums.OperationType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

//自定义注解，用于标识某个方法需要进行公共字段的自动填充处理
@Target(ElementType.METHOD)//该注解只应用于方法
@Retention(RetentionPolicy.RUNTIME)
public @interface AutoFill {
    //数据库操作类型：UPDATE INSERT
    OperationType value();
}
