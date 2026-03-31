package com.dohui.user_service.global.exception;

public class EmailAlreadyExistsException extends RuntimeException{
    public EmailAlreadyExistsException(){
        super("사용 중인 이메일입니다");
    }
}
