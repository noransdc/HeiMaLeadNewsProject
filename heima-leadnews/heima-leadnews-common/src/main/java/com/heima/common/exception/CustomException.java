package com.heima.common.exception;

import com.heima.model.common.enums.AppHttpCodeEnum;

public class CustomException extends RuntimeException {

    private AppHttpCodeEnum appHttpCodeEnum;
    private String errorMsg;

    public CustomException(AppHttpCodeEnum appHttpCodeEnum){
        this.appHttpCodeEnum = appHttpCodeEnum;
    }

    public CustomException(AppHttpCodeEnum appHttpCodeEnum, String errorMsg){
        this.appHttpCodeEnum = appHttpCodeEnum;
        this.errorMsg = errorMsg;
    }

    public AppHttpCodeEnum getAppHttpCodeEnum() {
        return appHttpCodeEnum;
    }

    public String getErrorMsg(){
        return this.errorMsg;
    }


}
