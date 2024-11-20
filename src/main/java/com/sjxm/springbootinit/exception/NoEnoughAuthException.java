package com.sjxm.springbootinit.exception;

public class NoEnoughAuthException extends BaseException{
    public NoEnoughAuthException(){

    }

    public NoEnoughAuthException(String msg){
        super(msg);
    }
}
