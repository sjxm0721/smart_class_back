package com.sjxm.springbootinit.exception;

/**
 * @Author: 四季夏目
 * @Date: 2024/11/24
 * @Description:
 */
public class SubmitNotFoundException extends BaseException{
    public SubmitNotFoundException(){}

    public SubmitNotFoundException(String str){
        super(str);
    }
}
