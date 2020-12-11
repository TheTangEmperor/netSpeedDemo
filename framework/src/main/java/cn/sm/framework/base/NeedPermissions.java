package cn.sm.framework.base;

import android.Manifest;

/**
 * Created by shiminli on 2020/2/10.
 */

public class NeedPermissions {

    /**
     * 需要进行检测的权限数组
     */
    public static final String[] permissions = {
//            <!-- 允许程序访问CellID或WiFi热点来获取粗略的位置 -->
//            Manifest.permission.ACCESS_COARSE_LOCATION,
//          获取位置权限
//            Manifest.permission.ACCESS_FINE_LOCATION,
//            拨打电话权限
//            Manifest.permission.CALL_PHONE,
//            相机权限
//            Manifest.permission.CAMERA,
//            读取和写入本地文件权限
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.REQUEST_INSTALL_PACKAGES
//            允许程序读写手机状态和身份
//            Manifest.permission.READ_PHONE_STATE
    };
}
