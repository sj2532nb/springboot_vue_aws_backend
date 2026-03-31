package com.dohui.user_service.global.exception;

public class InvalidPasswordException extends RuntimeException{
    public InvalidPasswordException(){
        super("현재 비밀번호가 일치하지 않습니다.");
    }
}
