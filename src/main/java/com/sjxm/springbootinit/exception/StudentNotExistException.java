package com.sjxm.springbootinit.exception;

public class StudentNotExistException extends BaseException{
    public StudentNotExistException(){}

    public StudentNotExistException(String msg){
        super(msg);
    }
}
