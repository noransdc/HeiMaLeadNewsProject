package com.heima.schedule.constant;

public enum TaskStatusEnum {

    INIT(0),
    RUNNING(1),
    SUCCESS(2),
    FAIL(3),
    DEAD(4),


    ;


    private int code;

    TaskStatusEnum(int code){
        this.code = code;
    }

    public int getCode() {
        return code;
    }


    public static TaskStatusEnum codeOf(int code){
        for (TaskStatusEnum value : values()) {
            if (value.getCode() == code){
                return value;
            }
        }
        return null;
    }

    public boolean canTransitTo(TaskStatusEnum target){
        switch (this){
            case INIT:
                return target == RUNNING;

            case RUNNING:
                return target == SUCCESS || target == FAIL || target == DEAD;

            case FAIL:
                return target == RUNNING; // retry

            case SUCCESS:
            case DEAD:
            default:
                return false;
        }
    }

}
