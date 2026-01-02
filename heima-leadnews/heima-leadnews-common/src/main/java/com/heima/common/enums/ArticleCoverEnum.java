package com.heima.common.enums;



public enum ArticleCoverEnum {


    NONE(0),
    SINGLE(1),
    MULTIPLE(3),
    AUTO(-1)
    ;



    private int code;

    ArticleCoverEnum(int code){
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }


    public static ArticleCoverEnum codeOf(int code){
        for (ArticleCoverEnum value : values()) {
            if (value.getCode() == code){
                return value;
            }
        }
        return null;
    }


}
