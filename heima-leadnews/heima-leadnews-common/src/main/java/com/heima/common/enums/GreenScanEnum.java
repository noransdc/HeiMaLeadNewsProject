package com.heima.common.enums;



public enum GreenScanEnum {

    PASS(0),
    PENDING_REVIEW(1),
    HIGH_RISK(2),
    SERVER_ERROR(3),

    ;



    private int code;

    GreenScanEnum(int code){
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public static GreenScanEnum codeOf(int code){
        for (GreenScanEnum value : values()) {
            if (value.code == code){
                return value;
            }
        }
        return null;
    }


}
