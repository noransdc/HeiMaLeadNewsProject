package com.heima.user.constant;


public enum EventOutboxStatusEnum {

    NEW(0),
    SENT(1),
    FAILED(2),
    SENDING(3),

    ;


    private int code;

    EventOutboxStatusEnum(int code){
        this.code = code;
    }


    public int getCode() {
        return code;
    }

    public static EventOutboxStatusEnum codeOf(int code){
        for (EventOutboxStatusEnum value : values()) {
            if (value.getCode() == code){
                return value;
            }
        }
        return null;
    }

    public boolean canTransitTo(EventOutboxStatusEnum target){
        switch (this){
            case NEW:
                return target == SENDING;

            case SENDING:
                return target == SENT || target == FAILED;

            case FAILED:
                return target == SENDING;

            case SENT:
            default:
                return false;
        }
    }

}
