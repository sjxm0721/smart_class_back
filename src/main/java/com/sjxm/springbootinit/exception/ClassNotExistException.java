package com.sjxm.springbootinit.exception;

public class ClassNotExistException extends BaseException{

    public ClassNotExistException(){

    }

    public ClassNotExistException(String msg){
        super(msg);
    }
}
