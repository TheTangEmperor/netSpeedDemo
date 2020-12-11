package cn.sm.framework.util;

import android.content.Context;
import android.net.TrafficStats;

public class FlowStats {

    private long lastTotalRxBytes;
    private long lastTimeStamp;

    public String getNetSpeedToMB(Context context){
        String netSpeed;
        long speed = absKb(context);
        if (speed == 0){
            return "0 MB/s";
        }
        netSpeed = String.format("%.2f", new Float(speed / 1024f)) + " MB/s";
        return netSpeed;
    }

    public String getNetSpeedToKB(Context context){
        String netSpeed;
        long speed = absKb(context);
        netSpeed = speed + " KB/s";
        return netSpeed;
    }

    private long absKb(Context context){
        try{
            long nowTotalRxBytes = TrafficStats.getTotalRxBytes() / 1024; // kb
            long nowTimeStamp = System.currentTimeMillis();
            long speed = (nowTotalRxBytes - lastTotalRxBytes) * 1000 / (nowTimeStamp - lastTimeStamp);
            lastTotalRxBytes = nowTotalRxBytes;
            lastTimeStamp = nowTimeStamp;
            return speed;
        }catch (Exception e){
            System.out.println(e.getMessage());
            return 0;
        }

    }
}
