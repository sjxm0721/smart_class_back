package com.sjxm.springbootinit.exception;

public class DeviceNotExistException extends BaseException{

    public DeviceNotExistException(){

    }

    public DeviceNotExistException(String msg){
        super(msg);
    }
}
