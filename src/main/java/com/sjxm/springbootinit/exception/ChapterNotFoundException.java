package com.sjxm.springbootinit.exception;

/**
 * @Author: 四季夏目
 * @Date: 2024/11/29
 * @Description:
 */
public class ChapterNotFoundException extends BaseException{

    public ChapterNotFoundException(){}

    public ChapterNotFoundException(String msg){
        super(msg);
    }

}
