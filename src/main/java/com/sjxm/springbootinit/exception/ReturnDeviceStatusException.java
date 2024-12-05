package com.sjxm.springbootinit.exception;

/**
 * 账号不存在异常
 */
public class ReturnDeviceStatusException extends BaseException {

    public ReturnDeviceStatusException() {
    }

    public ReturnDeviceStatusException(String msg) {
        super(msg);
    }

}
