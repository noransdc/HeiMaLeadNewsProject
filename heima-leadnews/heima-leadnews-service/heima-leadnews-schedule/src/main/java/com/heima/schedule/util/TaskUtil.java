package com.heima.schedule.util;


public class TaskUtil {


    public static String getBizKey(String action, Object key){
        return action + ":" + key;
    }


}
