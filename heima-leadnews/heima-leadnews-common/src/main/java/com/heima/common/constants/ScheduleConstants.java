package com.heima.common.constants;

public class ScheduleConstants {

    //task状态
    public static final int PENDING = 0;   //初始化状态

    public static final int RUNNING = 1;   //执行中

    public static final int SUCCESS = 2;       //已执行状态

    public static final int FAILED = 3;   //已取消状态

    public static String FUTURE = "future_";   //未来数据key前缀

    public static String TOPIC = "topic_";     //当前数据key前缀
}