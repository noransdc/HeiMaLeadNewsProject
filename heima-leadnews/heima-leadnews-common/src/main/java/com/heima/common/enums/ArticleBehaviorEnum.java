package com.heima.common.enums;



public enum ArticleBehaviorEnum {


    VIEW(1),
    LIKE(2),
    DISLIKE(3),
    COMMENT(4),
    ADD_COLLECT(5),
    CANCEL_COLLECT(6),

    ;


    private int code;

    ArticleBehaviorEnum(int code){
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public static ArticleBehaviorEnum codeOf(int code){
        for (ArticleBehaviorEnum value : values()) {
            if (value.getCode() == code){
                return value;
            }
        }
        return null;
    }


}
