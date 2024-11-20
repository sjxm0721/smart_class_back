package com.sjxm.springbootinit.aop;


import com.sjxm.springbootinit.annotation.AutoFill;
import com.sjxm.springbootinit.constant.AutoFillConstant;
import com.sjxm.springbootinit.context.BaseContext;
import com.sjxm.springbootinit.model.enums.OperationType;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDateTime;

//自定义切面，实现公共字段自动填充
@Aspect //切面类
@Component //交由spring管理
@Slf4j
public class AutoFillAspect {

    //切入点
    @Pointcut("execution(* com.sjxm.springbootinit.mapper.*.*(..)) && @annotation(com.sjxm.springbootinit.annotation.AutoFill)") //切点表达式
    public void autoFillPointCut(){}

    //前置通知，在通知中进行公共字段的赋值
    @Before("autoFillPointCut()")
    public void autoFill(JoinPoint joinPoint) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        log.info("开始进行公共字段的自动填充");

        //获取当前被拦截的方法上的数据库操作类型
        MethodSignature signature =(MethodSignature) joinPoint.getSignature();//方法签名对象
        AutoFill autoFill = signature.getMethod().getAnnotation(AutoFill.class);//获得方法上的注解对象
        OperationType operationType = autoFill.value();//获得数据库操作类型

        //获取当前被拦截方法的参数--实体对象
        Object[] args = joinPoint.getArgs();
        if(args==null||args.length==0){
            return;
        }

        Object entity=args[0];

        //准备赋值的数据
        LocalDateTime now = LocalDateTime.now();
        String currentId = BaseContext.getCurrentId();

        //根据当前不同的操作类型，为对应的属性进行赋值
        if(operationType==OperationType.INSERT){
            //插入操作
            Method setCreateTime = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_CREATE_TIME, LocalDateTime.class);
            Method setCreateUser = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_CREATE_USER, String.class);
            Method setUpdateTime = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class);
            Method setUpdateUser = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_USER, String.class);

            setCreateTime.invoke(entity,now);
            setCreateUser.invoke(entity,currentId);
            setUpdateTime.invoke(entity,now);
            setUpdateUser.invoke(entity,currentId);
        }
        else if(operationType==OperationType.UPDATE){
            //更新操作
            Method setUpdateTime = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class);
            Method setUpdateUser = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_USER, String.class);

            setUpdateTime.invoke(entity,now);
            setUpdateUser.invoke(entity,currentId);

        }
    }
}
