package cn.sm.framework.net.download;

public interface DownloadObserver {

    void onDownloadStateChanged(DownLoadInfo info);
    void onDownloadProgressed(DownLoadInfo info);
    DownLoadInfo getDownLoadInfo();
}
