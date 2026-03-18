package com.dohui.user_service.global.exception;

public class AlreadyLikedException extends RuntimeException{
    public AlreadyLikedException(){
        super("추천은 한 번 만 할 수 있습니다");
    }
}
