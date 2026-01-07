package com.heima.common.enums;


public enum ArticlePublishRspEnum {

    NOT_READY(0),
    SUCCESS(1),
    FAIL(2),

    ;


    private int code;


    ArticlePublishRspEnum (int code){
        this.code = code;
    }

    public int getCode() {
        return code;
    }


}
