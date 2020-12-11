package cn.sm.framework;

import android.app.Application;

import java.lang.reflect.Method;

public class WorkApp {

    private static Application app;


    public static void init(Application application){
        app = application;
    }

    public static Application getApp() {
        if (app == null){
            app = getApplicationInner();
        }
        return app;
    }

    public static void setApp(Application app) {
        WorkApp.app = app;
    }

    /**
     * 通过反射ActivityThread线程得到当前application上下文
     * @return 上下文
     */
    private static Application getApplicationInner() {
        try {
            Class<?> activityThread = Class.forName("android.app.ActivityThread");

            Method currentApplication = activityThread.getDeclaredMethod("currentApplication");
            Method currentActivityThread = activityThread.getDeclaredMethod("currentActivityThread");

            Object current = currentActivityThread.invoke((Object)null);
            Object app = currentApplication.invoke(current);

            return (Application)app;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
