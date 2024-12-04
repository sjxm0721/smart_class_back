package com.sjxm.springbootinit.exception;

/**
 * 账号被锁定异常
 */
public class BorrowNotFoundException extends BaseException {

    public BorrowNotFoundException() {
    }

    public BorrowNotFoundException(String msg) {
        super(msg);
    }

}
