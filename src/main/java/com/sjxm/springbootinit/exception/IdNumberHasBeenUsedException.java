package com.sjxm.springbootinit.exception;

public class IdNumberHasBeenUsedException extends BaseException{

    public IdNumberHasBeenUsedException(){}

    public IdNumberHasBeenUsedException(String msg){
        super(msg);
    }
}
