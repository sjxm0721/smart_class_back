package com.sjxm.springbootinit.exception;

public class SchoolNotExistException extends BaseException{
    public SchoolNotExistException(){

    }

    public SchoolNotExistException(String msg){
        super(msg);
    }
}
