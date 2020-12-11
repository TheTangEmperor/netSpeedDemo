package cn.sm.framework.bean;

import android.graphics.drawable.Drawable;

/**
 * 跳转安装应用 用到的实体类
 *
 * @author xzhang
 */

public class AppInfo {

    private String name ;
    private String packageName ;
    private Drawable icon ;
    private long firstInstallTime ;
    private String versionName ;
    private int versionCode;

    public AppInfo() {
    }

    public AppInfo(String name, String packageName, Drawable icon, long firstInstallTime, String versionName, int versionCode) {
        this.name = name;
        this.packageName = packageName;
        this.icon = icon;
        this.firstInstallTime = firstInstallTime;
        this.versionName = versionName;
        this.versionCode = versionCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    public long getFirstInstallTime() {
        return firstInstallTime;
    }

    public void setFirstInstallTime(long firstInstallTime) {
        this.firstInstallTime = firstInstallTime;
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public int getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(int versionCode) {
        this.versionCode = versionCode;
    }


}
