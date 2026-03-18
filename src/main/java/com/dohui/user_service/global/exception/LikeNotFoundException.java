package com.dohui.user_service.global.exception;

public class LikeNotFoundException extends RuntimeException{
    public LikeNotFoundException(){
        super("취소할 좋아요가 존재하지 않습니다.");
    }
}
