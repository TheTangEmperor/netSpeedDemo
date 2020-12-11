package com.netspeed.demo;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;
import cn.sm.framework.net.download.DownLoadInfo;
import cn.sm.framework.net.download.DownLoadState;
import cn.sm.framework.net.download.DownloadObserver;
import cn.sm.framework.net.download.HttpDownloadManager;

public class NetFlowTextView extends AppCompatTextView implements DownloadObserver {
    public NetFlowTextView(@NonNull Context context) {
        super(context);
    }

    public NetFlowTextView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public NetFlowTextView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    /**
     * 当前控件显示时注册观察事件
     */
    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        HttpDownloadManager.getInstance().registerObserver(this);
    }


    /**
     * 当前控件消失时取消注册观察事件
     */
    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        HttpDownloadManager.getInstance().unRegisterObserver(this);
    }

    //    正在下载的应用信息
    private DownLoadInfo downLoadInfo;

    public void setDownLoadInfo(DownLoadInfo downLoadInfo) {
        this.downLoadInfo = downLoadInfo;
    }

    @Override
    public void onDownloadStateChanged(DownLoadInfo info) {
        this.downLoadInfo = info;
        if (info.getStateInte() == DownLoadState.FINISH.getState()){
            setText("0MB/s 剩余时间0秒");
        }
        downLoadInfo.setReadLength(0);
    }

    @Override
    public void onDownloadProgressed(DownLoadInfo info) {
        this.downLoadInfo = info;
        long secondCount = info.getSecondCount();
        float m = secondCount / 1024f / 1024f;
        int surplus = (int) ((info.getCountLength() - info.getReadLength()) / secondCount);
        String text = String.format("%.1f", m) + "MB/s 剩余时间" + surplus + "秒 已下载 " + info.getPercentage() + "%";
        setText(text);
        System.out.println(text);
    }

    @Override
    public DownLoadInfo getDownLoadInfo() {
        return downLoadInfo;
    }

}
