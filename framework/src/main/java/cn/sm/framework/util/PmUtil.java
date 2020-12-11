package cn.sm.framework.util;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import cn.sm.framework.WorkApp;
import cn.sm.framework.bean.AppInfo;


public class PmUtil {

    private static final String SCHEME = "package";

    /**
     * 获取手机上所有已安装的非系统应用
     * @param context 上下文
     * @return 已安装列表
     */
    public static List<AppInfo> getAppInfos() {
        List<AppInfo> appInfoList = new ArrayList<>();

        //获取包管理器
        PackageManager pm = WorkApp.getApp().getPackageManager();
        //获取已安装的包信息
        List<PackageInfo> packageInfos = pm.getInstalledPackages(0);
        String pritStr = "";
        for (PackageInfo packageInfo : packageInfos) {
            if ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0){
//                System.out.println("systemApp: " + packageInfo.applicationInfo.loadLabel(pm).toString());
                continue;
            }
            pritStr = pritStr + "  " + packageInfo.applicationInfo.loadLabel(pm).toString();
            //获取包名
            String packageName = packageInfo.packageName;
            //获取应用图标
            Drawable icon = packageInfo.applicationInfo.loadIcon(pm);
            //获取应用的名称
            String name = packageInfo.applicationInfo.loadLabel(pm).toString();
            //获取第一次安装的时间
            long firstInstallTime = packageInfo.firstInstallTime;
            //获取版本号
            int versionCode = packageInfo.versionCode;
            //获取版本名称
            String versionName = packageInfo.versionName;

            AppInfo appInfo = new AppInfo(name, packageName, icon, firstInstallTime, versionName, versionCode);
            appInfoList.add(appInfo);
        }
        System.out.println(pritStr);
        return appInfoList;
    }


    /***
     * 获取ApplicationInfo
     *
     * @param context
     * @return
     */
    public static ApplicationInfo getApplicationInfo(Context context) {

        ApplicationInfo applicationInfo = null;
        try {
            applicationInfo = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }

        return applicationInfo;
    }

    /**
     * 获取应用名称
     * @param context
     * @return
     */
    public static String getAppName(Context context){
        ApplicationInfo applicationInfo = getApplicationInfo(context);
        return context.getPackageManager().getApplicationLabel(applicationInfo).toString();
    }




    public static <T> T getMetaDate(Context context, String packageName, String metaDateName) {
        ApplicationInfo info = getApplicationInfo(context, packageName);
        T t = null;
        if (info != null && info.metaData != null) {
            t = (T) info.metaData.get(metaDateName);
        }
        return t;
    }

    /***
     * 获取指定包名得ApplicationInfo
     *
     * @param context
     * @return
     */
    public static ApplicationInfo getApplicationInfo(Context context, String packageName) {

        ApplicationInfo applicationInfo = null;
        try {
            applicationInfo = context.getPackageManager().getApplicationInfo(packageName, PackageManager.GET_META_DATA);
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }

        return applicationInfo;
    }

    /**
     * 获取应用的versioncode
     * @param context
     * @return
     */
    public static int getVersionCode(@NonNull Context context){
        PackageManager manager = context.getPackageManager();
        int code = 0;
        try {
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            code = info.versionCode;

        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        return code;
    }


    /**
     * 获取应用的versionName
     * @param context
     * @return
     */
    public static String getVersionName(@NonNull Context context){
        PackageManager manager = context.getPackageManager();
        String versionname = "";
        try {
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            versionname = info.versionName;

        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionname;
    }


    /**
     * 安装应用程序的方法
     */
    public static void onInstall(String path, Context context) {
        if (context == null){
            System.out.println("context is null");
            return;
        }
        File file = new File(path);
        if (file.exists()) {
//            给文件设置可读可写权限
            try {
                String[] command1 = {"chmod", "777", file.getAbsolutePath()};
                ProcessBuilder builder = new ProcessBuilder(command1);
                builder.start();

            } catch (Exception e) {
                e.printStackTrace();
            }

            boolean success = false;
            Intent intent = new Intent(Intent.ACTION_VIEW);
            File apkFile = new File(path);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            先尝试使用最原始的方式打开
            try{
                intent.setDataAndType(Uri.fromFile(apkFile), "application/vnd.android.package-archive");
                context.startActivity(intent);
                success = true;
            }catch (Exception e){
                success = false;
            }
            if (success) return;
//          再尝试使用 最新的打开方式打开
            try{
                //  7.0以上需要使用content:// 的路径
                intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                Uri uri = FileProvider.getUriForFile(context, context.getPackageName() + ".fileprovider", apkFile);
                intent.setDataAndType(uri, "application/vnd.android.package-archive");
                context.startActivity(intent);
            }catch (Exception e){
                e.printStackTrace();
                Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
            }

        } else {
            System.out.println("install file is not exists");
        }
    }


    /**
     * 打开对应到app
     */
    public static void openApp(String packageName, Context context) {
        if (context == null){
            System.out.println("context is null");
            return;
        }
        try {
            PackageManager packageManager = context.getPackageManager();
            Intent intentForPackage = packageManager.getLaunchIntentForPackage(packageName);
            context.startActivity(intentForPackage);

        } catch (Exception e) {

        }
    }


    /**
     * 通过包名查询是否已经安装过该app  true已经安装   false没有安装
     *
     * @param context
     * @param packageName
     * @return
     */
    public static boolean appExists(Context context, String packageName) {
        //获取包管理器
        PackageManager packageManager = context.getPackageManager();
        //通过包名获取Intent
        Intent it = packageManager.getLaunchIntentForPackage(packageName);

        return it == null ? false : true;
    }

    /**
     * 卸载指定包名得应用
     * @param context 上下
     * @param packageName 包名
     */
    public static void unInstallApp(String packageName, Context context) {
        Uri uri = Uri.fromParts("package", packageName, null);
        Intent intent = new Intent(Intent.ACTION_DELETE, uri);
        context.startActivity(intent);
    }


    /**
     * 通过包名打开对应APP得系统设置界面
     * @param context
     * @param packageName
     */
    public static void showInstalledAppSetting(Context context, String packageName) {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri = Uri.fromParts(SCHEME, packageName, null);
        intent.setData(uri);
        context.startActivity(intent);
    }


    /**
     * 得到指定apk得icon
     * @param context 上下文
     * @param apkFilepath 路径
     * @return icon
     */
    public static Drawable getAkIcon(Context context, String apkFilepath) {
        PackageManager pm = context.getPackageManager();
        PackageInfo pkgInfo = getPackageInfo(context, apkFilepath);
        if (pkgInfo == null) {
            return null;
        }

        ApplicationInfo appInfo = pkgInfo.applicationInfo;
        if (Build.VERSION.SDK_INT >= 8) {
            appInfo.sourceDir = apkFilepath;
            appInfo.publicSourceDir = apkFilepath;
        }
        return pm.getApplicationIcon(appInfo);
    }

    /**
     * 得到指定apk的名称
     * @param context 上下文
     * @param apkFilepath 路径
     * @return 名称
     */
    public static CharSequence getApkLabel(Context context, String apkFilepath) {
        PackageManager pm = context.getPackageManager();
        PackageInfo pkgInfo = getPackageInfo(context, apkFilepath);
        if (pkgInfo == null) {
            return null;
        }
        ApplicationInfo appInfo = pkgInfo.applicationInfo;
        if (Build.VERSION.SDK_INT >= 8) {
            appInfo.sourceDir = apkFilepath;
            appInfo.publicSourceDir = apkFilepath;
        }
        return pm.getApplicationLabel(appInfo);
    }


    //得到PackageInfo对象，其中包含了该apk包含的activity和service
    public static PackageInfo getPackageInfo(Context context, String apkFilepath) {
        PackageManager pm = context.getPackageManager();
        PackageInfo pkgInfo = null;
        try {
            pkgInfo = pm.getPackageArchiveInfo(apkFilepath, PackageManager.GET_ACTIVITIES | PackageManager.GET_SERVICES);
        } catch (Exception e) {
            // should be something wrong with parse
            e.printStackTrace();
        }
        return pkgInfo;
    }

}
