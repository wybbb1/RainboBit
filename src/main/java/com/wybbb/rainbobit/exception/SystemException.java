package com.wybbb.rainbobit.exception;

import com.wybbb.rainbobit.common.enums.AppHttpCodeEnum;
import lombok.Getter;

@Getter
public class SystemException extends RuntimeException{

    private int code;

    private String msg;

    public SystemException(AppHttpCodeEnum httpCodeEnum) {
        super(httpCodeEnum.getMsg());
        this.code = httpCodeEnum.getCode();
        this.msg = httpCodeEnum.getMsg();
    }

    public SystemException(String msg) {
        this.code = 500;
        this.msg = msg;
    }
    
}
