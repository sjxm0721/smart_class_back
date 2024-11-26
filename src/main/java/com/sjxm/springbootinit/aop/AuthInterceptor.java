package com.sjxm.springbootinit.aop;

import com.sjxm.springbootinit.annotation.AuthCheck;
import com.sjxm.springbootinit.constant.MessageConstant;
import com.sjxm.springbootinit.exception.AccountLockedException;
import com.sjxm.springbootinit.exception.NoEnoughAuthException;
import com.sjxm.springbootinit.model.entity.Account;
import com.sjxm.springbootinit.model.enums.AccountRoleEnum;
import com.sjxm.springbootinit.service.AccountService;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Objects;

/**
 * 权限校验 AOP
 *
 * @author <a href="https://github.com/sjxm0721">四季夏目</a>

 */
@Aspect
@Component
public class AuthInterceptor {

    @Resource
    private AccountService accountService;

    /**
     * 执行拦截
     *
     * @param joinPoint
     * @param authCheck
     * @return
     */
    @Around("@annotation(authCheck)")
    public Object doInterceptor(ProceedingJoinPoint joinPoint, AuthCheck authCheck) throws Throwable {
        String mustRole = authCheck.mustRole();
        // 当前登录用户
        Account account = accountService.getLoginUser();
        // 必须有该权限才通过
        if (StringUtils.isNotBlank(mustRole)) {
            AccountRoleEnum mustAccountRoleEnum = AccountRoleEnum.getEnumByValue(mustRole);
            if (mustAccountRoleEnum == null) {
                throw new NoEnoughAuthException(MessageConstant.NO_ENOUGH_AUTH);
            }
            Integer auth = account.getAuth();
            // 如果被封号，直接拒绝
            if(!Objects.equals(mustAccountRoleEnum.getValue(), auth)){
                throw new AccountLockedException(MessageConstant.ACCOUNT_LOCKED);
            }
        }
        // 通过权限校验，放行
        return joinPoint.proceed();
    }
}

