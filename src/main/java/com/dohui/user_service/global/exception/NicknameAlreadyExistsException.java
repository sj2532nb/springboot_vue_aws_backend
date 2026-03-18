package com.dohui.user_service.global.exception;

public class NicknameAlreadyExistsException extends RuntimeException{
    public NicknameAlreadyExistsException(){
        super("이미 사용 중인 닉네임입니다.");
    }
}
