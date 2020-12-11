package cn.sm.framework.util;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.view.WindowManager;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;

/**
 * Created by shiminli on 2019/7/25.
 * 获取关于设备的信息
 */

public class EquipmentUtil {


    /**
     * 获取当前手机系统语言。
     *
     * @return 返回当前系统语言。例如：当前设置的是“中文-中国”，则返回“zh-CN”
     */
    public static String getSystemLanguage() {
        return Locale.getDefault().getLanguage();
    }

    /**
     * 获取当前系统上的语言列表(Locale列表)
     *
     * @return 语言列表
     */
    public static Locale[] getSystemLanguageList() {
        return Locale.getAvailableLocales();
    }

    /**
     * 获取当前手机系统版本号
     *
     * @return 系统版本号
     */
    public static String getSystemVersion() {
        return Build.VERSION.RELEASE;
    }

    /**
     * 获取手机型号
     *
     * @return 手机型号
     */
    public static String getSystemModel() {
        return Build.MODEL;
    }

    /**
     * 获取手机设备名
     *
     * @return 手机设备名
     */
    public static String getSystemDevice() {
        return Build.DEVICE;
    }

    /**
     * 获取手机设备名
     *
     * @return 手机设备名
     */
    public static String getDeviceNickName() {
        String nickname = "";
        try{
            BluetoothAdapter myDevice = BluetoothAdapter.getDefaultAdapter();
            nickname = myDevice.getName();
        }catch (Exception e){
            e.printStackTrace();
        }

        return nickname;
    }

    /**
     * 获取手机厂商
     *
     * @return 手机厂商
     */
    public static String getDeviceBrand() {
        return Build.BRAND;
    }

    /**
     * 获取手机主板名
     *
     * @return 主板名
     */
    public static String getDeviceBoand() {
        return Build.BOARD;
    }


    /**
     * 获取手机厂商名
     *
     * @return 手机厂商名
     */
    public static String getDeviceManufacturer() {
        return Build.MANUFACTURER;
    }


    /**
     * 获取手机IMEI(需要“android.permission.READ_PHONE_STATE”权限)
     *
     * @return 手机IMEI
     */
    public static String getIMEI(Context ctx) {

        try{
            TelephonyManager tm = (TelephonyManager) ctx.getSystemService(Activity.TELEPHONY_SERVICE);
            if (tm != null) {

                return tm.getDeviceId();
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        return "null";
    }


    /**
     * 得到有线MAC地址
     *
     * @return
     */
    public static String getWan0MacAddress(Context context) {

        String macSerial = null;
        String str = "";
        try {
            Process pp = Runtime.getRuntime().exec(
                    "cat /sys/class/net/wlan0/address ");
            InputStreamReader ir = new InputStreamReader(pp.getInputStream());
            LineNumberReader input = new LineNumberReader(ir);


            for (; null != str; ) {
                str = input.readLine();
                if (str != null) {
                    macSerial = str.trim();// 去空格
                    break;
                }
            }
        } catch (IOException ex) {
            // 赋予默认值
            ex.printStackTrace();
        }
        return macSerial;

    }

    /**
     * 得到有线MAC地址
     *
     * @return
     */
    public static String getEth0MacAddress(Context context) {

        String macSerial = null;
        String str = "";
        try {
            Process pp = Runtime.getRuntime().exec(
                    "cat /sys/class/net/eth0/address ");
            InputStreamReader ir = new InputStreamReader(pp.getInputStream());
            LineNumberReader input = new LineNumberReader(ir);


            for (; null != str; ) {
                str = input.readLine();
                if (str != null) {
                    macSerial = str.trim();// 去空格
                    break;
                }
            }
        } catch (IOException ex) {
            // 赋予默认值
            ex.printStackTrace();
        }
        return macSerial;

    }


    /**
     * 得到无线MAC地址
     *
     * @return
     */
    public static String getWifiMacAddress(Context context) {

        String mac = null;
        WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = null;
        if (wifi != null) {
            info = wifi.getConnectionInfo();
        }
        if (info != null) {
            mac = info.getMacAddress();
        }
        return mac;

    }


    /**
     * 获取mac地址
     * @param context 上下文
     * @return mac地址
     */
    public static String getMacAddress(Context context) {
        String macAddress = null;
        macAddress = getLocalInetAddress();
        System.out.println("macAddress: " + macAddress);
        if (!TextUtils.isEmpty(macAddress) && !"02:00:00:00:00:00".equals(macAddress)){
            return macAddress;
        }
        macAddress = getWan0MacAddress(context);
        System.out.println("macAddress: " + macAddress);
        // 如果是真实的mac地址 直接返回
        if (!TextUtils.isEmpty(macAddress) && !"02:00:00:00:00:00".equals(macAddress)){
            return macAddress;
        }
        macAddress = getWifiMacAddress(context);
        System.out.println("macAddress: " + macAddress);
        // 如果是真实的mac地址 直接返回
        if (!TextUtils.isEmpty(macAddress) && !"02:00:00:00:00:00".equals(macAddress)){
            return macAddress;
        }
        macAddress = getMacAboveVersion6();
        System.out.println("macAddress: " + macAddress);
        // 如果是真实的mac地址 直接返回
        if (!TextUtils.isEmpty(macAddress) && !"02:00:00:00:00:00".equals(macAddress)){
            return macAddress;
        }


        return null;
    }


    /* 获取mac地址有一点需要注意的就是android 6.0版本后，以下注释方法不再适用，不管任何手机都会返回"02:00:00:00:00:00"这个默认的mac地址，
        这是googel官方为了加强权限管理而禁用了getSYstemService(Context.WIFI_SERVICE)方法来获得mac地址。
    * */
    public static String getMacAboveVersion6(){

        String macAddress = "";
        StringBuffer buf = new StringBuffer();
        NetworkInterface networkInterface = null;
        try {
            networkInterface = NetworkInterface.getByName("eth1");
            if (networkInterface == null) {
                networkInterface = NetworkInterface.getByName("wlan0");
            }
            if (networkInterface == null) {
                return macAddress;
            }
            byte[] addr = networkInterface.getHardwareAddress();
            for (byte b : addr) {
                buf.append(String.format("%02X:", b));
            }
            if (buf.length() > 0) {
                buf.deleteCharAt(buf.length() - 1);
            }
            macAddress = buf.toString();
        } catch (SocketException e) {
            e.printStackTrace();
            return macAddress;
        }
        return macAddress;
    }


    /**
     * 通过当前设备已获得的ip地址中得到设备的mac地址
     * @return
     */
    public static String getLocalInetAddress() {
        String strMacAddr = null;
        try {
            // 获得IpD地址
            InetAddress ip  = null;
            // 从已获取的ip中列举出有效的地址
            Enumeration<NetworkInterface> en_netInterface = NetworkInterface.getNetworkInterfaces();
            while (en_netInterface.hasMoreElements()) {// 是否还有元素
                NetworkInterface ni = (NetworkInterface) en_netInterface.nextElement();// 得到下一个元素
                Enumeration<InetAddress> en_ip = ni.getInetAddresses();// 得到一个ip地址的列举
                while (en_ip.hasMoreElements()) {
                    ip = en_ip.nextElement();
                    if (!ip.isLoopbackAddress() && ip.getHostAddress().indexOf(":") == -1)
                        break;
                    else
                        ip = null;
                }
                if (ip != null) {
                    break;
                }
            }

            byte[] b = NetworkInterface.getByInetAddress(ip).getHardwareAddress();
            StringBuffer buffer = new StringBuffer();
            for (int i = 0; i < b.length; i++) {
                if (i != 0) {
                    buffer.append(':');
                }
                String str = Integer.toHexString(b[i] & 0xFF);
                buffer.append(str.length() == 1 ? 0 + str : str);
            }
            strMacAddr = buffer.toString().toUpperCase();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return strMacAddr;
    }


    /**
     * 获取屏幕宽度
     *
     * @param context
     * @return
     */
    public static int getDevicesWidth(Context context) {
        if (context == null) return -1;
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        if (wm == null) return -1;
        return wm.getDefaultDisplay().getWidth();
    }


    /**
     * 获取屏幕高度
     *
     * @param context
     * @return
     */
    public static int getDevicesHeight(Context context) {
        if (context == null) return -1;
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        if (wm == null) return -1;
        return wm.getDefaultDisplay().getHeight();
    }


    /**
     * 通过application获取栈顶的activity实例
     * @param application
     * @return
     */
    public static Activity getTopActivity(Application application) {
        Activity activity = null;
        try {
            Class clz = application.getClass().forName("android.app.ActivityThread");
            Method meth = clz.getMethod("currentActivityThread");
            Object currentActivityThread = meth.invoke(null);
            Field f = clz.getDeclaredField("mActivities");
            f.setAccessible(true);
            ArrayMap obj = (ArrayMap) f.get(currentActivityThread);
            for (Object key : obj.keySet()) {
                Object activityRecord = obj.get(key);
                Field actField = activityRecord.getClass().getDeclaredField("activity");
                actField.setAccessible(true);
                activity = (Activity) actField.get(activityRecord);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return activity;
    }

    /**
     * 获取当前设备运行内存信息
     * @param context
     * @return
     */
    public static ActivityManager.MemoryInfo getRAMInfo(Context context){
        if (context == null) return null;
        ActivityManager.MemoryInfo memoryInfo = null;
        try{
            ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            memoryInfo = new ActivityManager.MemoryInfo();
            manager.getMemoryInfo(memoryInfo);
        }catch (Exception e){
            e.printStackTrace();
        }
        return memoryInfo;

    }

    /**
     * 获取手机内部总的存储空间
     *
     * @return
     */
    public static long getTotalInternalMemorySize() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long totalBlocks = stat.getBlockCount();
        return totalBlocks * blockSize;
    }


    /**
     * 获取手机内部剩余存储空间
     *
     * @return
     */
    public static long getAvailableInternalMemorySize() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long availableBlocks = stat.getAvailableBlocks();
        return availableBlocks * blockSize;
    }


    /**
     * 获取SDCARD剩余存储空间
     *
     * @return
     */
    public static long getAvailableSDCardMemorySize() {
        if (externalMemoryAvailable()) {
            File path = Environment.getExternalStorageDirectory();
            StatFs stat = new StatFs(path.getPath());
            long blockSize = stat.getBlockSize();
            long availableBlocks = stat.getAvailableBlocks();
            return availableBlocks * blockSize;
        }else {
            return 0;
        }
    }


    /**
     * 获取SDCARD剩余存储空间
     *
     * @return
     */
    public static long getTotalSDCardMemorySize() {
        if (externalMemoryAvailable()) {
            File path = Environment.getExternalStorageDirectory();
            StatFs stat = new StatFs(path.getPath());
            long blockSize = stat.getBlockSize();
            long totalBlocks = stat.getBlockCount();
            return totalBlocks * blockSize;
        }else {
            return 0;
        }
    }


    /**
     * SDCARD是否存
     */
    public static boolean externalMemoryAvailable() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }



    /**
     * 判断当前程序是否在前台
     *
     * @param context
     * @return
     */
    public static boolean isAppOnForeground(Context context) {
        try{
            ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
            if (appProcesses == null || appProcesses.size() <= 0) {
                System.out.println("appProcesses: " + appProcesses);
                return true;
            }
            for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
                if (appProcess == null) {
                    System.out.println("appProcess == null");
                    return true;
                } else if (appProcess.processName.equals(context.getPackageName())) {
                    System.out.println("importance = " + appProcess.importance);
                    return appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND || appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_VISIBLE;
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

}
