package cn.sm.framework.net.callback;

public interface DownloadListener {

    void onStart();

    void onProgress(int currentLength);

    void onFinish(String localPath);

    void onFailure();

}
