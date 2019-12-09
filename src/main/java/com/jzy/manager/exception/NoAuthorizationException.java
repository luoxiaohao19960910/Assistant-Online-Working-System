package com.jzy.manager.exception;

/**
 * @author JinZhiyun
 * @version 1.0
 * @ClassName NoAuthorizationException
 * @description 用户没有操作权限异常
 * @date 2019/11/19 18:38
 **/
public class NoAuthorizationException extends Exception {
    public NoAuthorizationException() {
    }

    public NoAuthorizationException(String message) {
        super(message);
    }
}
