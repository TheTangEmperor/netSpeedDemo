package com.netspeed.demo;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import cn.sm.framework.net.download.DownLoadInfo;
import cn.sm.framework.net.download.HttpDownloadManager;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private NetFlowTextView flowText;
    private DownLoadInfo info;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        flowText = findViewById(R.id.flowText);
        info = new DownLoadInfo();
        String packageName = "com.minitech.miniworld.TMobile.baidu";
        info.setId((long) packageName.hashCode());
        info.setPackageName(packageName);
        info.setAppName("迷你世界");
        info.setVersionCode("6700");
        info.setUrl("http://signd.bd.duoku.com/service/cloudapk_sign_online/59000/59776/59776_1601022543_GP60031.apk");
        info.setSavePath(getCacheDir().getAbsolutePath()+"/"+packageName+".apk");
        flowText.setDownLoadInfo(info);
        /**
         * "appName": "迷你世界",
         *     "ID": "4ea52730ab204016b0c14c68cf24738b",
         *     "size": 54311865,
         *     "md5": "7BE712E52CA3501D5AE05D7F69ECA5E2",
         *     "downCountDesc": "4.0亿",
         *     "hotTemp": "120",
         *     "icon": "http://gdown.baidu.com/img/0/512_512/58fef6897b01018d1300ff77ceed24fd.png",
         *     "cover": "http://appimg.hicloud.com/hwmarket/files/application/screenshut3/4ea52730ab204016b0c14c68cf24738b.jpg",
         *     "score": "4.8",
         *     "versionCode": "6700",
         *     "versionName": "6.7.0",
         *     "langguage": "CN",
         *     "gmPackageName": "com.minitech.miniworld.TMobile.baidu",
         *     "online": true,
         *     "downurl": "http://signd.bd.duoku.com/service/cloudapk_sign_online/59000/59776/59776_1601022543_GP60031.apk",
         */

    }

    @Override
    public void onClick(View view) {
        int tag = Integer.parseInt((String) view.getTag());
        if (tag == 1){
            HttpDownloadManager.getInstance().startAction(info);
        }else {
            HttpDownloadManager.getInstance().pause(info);
        }
    }
}