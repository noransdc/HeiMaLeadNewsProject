package com.heima.thread;


import com.heima.model.admin.pojos.AdminUser;

public class AdminThreadLocalUtil {


    private final static ThreadLocal<AdminUser> ADMIN_USER_THREAD_LOCAL = new ThreadLocal<>();


    public static void setUser(AdminUser user){
        ADMIN_USER_THREAD_LOCAL.set(user);
    }

    public static AdminUser getUser(){
        return ADMIN_USER_THREAD_LOCAL.get();
    }

    public static void clear(){
        ADMIN_USER_THREAD_LOCAL.remove();
    }


}
