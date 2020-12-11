package cn.sm.framework.net.callback;


import cn.sm.framework.db.DbManager;
import cn.sm.framework.net.download.DownLoadInfo;
import cn.sm.framework.net.download.DownLoadState;
import cn.sm.framework.net.download.HttpDownloadManager;
import rx.Subscriber;

/**
 * 断点下载处理类Subscriber
 * 用于在Http请求开始时，自动显示一个ProgressDialog
 * 在Http请求结束是，关闭ProgressDialog
 * 调用者自己对请求数据进行处理
 */
public class ProgressDownSubscriber<T> extends Subscriber<T> {

    private DownLoadInfo downInfo;

    public ProgressDownSubscriber(DownLoadInfo downInfo) {
        this.downInfo=downInfo;
    }



    @Override
    public void onCompleted() {
        HttpDownloadManager.getInstance().remove(downInfo);
        if (downInfo.getStateInte() == DownLoadState.DOWN.getState()){
            downInfo.setStateInte(DownLoadState.FINISH.getState());
            HttpDownloadManager.getInstance().notifyDownloadStateChanged(downInfo);
    //        这里需要将info信息写入到本地数据中，可自由扩展，用自己项目的数据库
            DbManager.getInstance().getDownloadDb().save(downInfo);
        }
    }

    @Override
    public void onError(Throwable e) {
        e.printStackTrace();
        HttpDownloadManager.getInstance().remove(downInfo);
        downInfo.setStateInte(DownLoadState.ERROR.getState());
        HttpDownloadManager.getInstance().notifyDownloadStateChanged(downInfo);
    }

    @Override
    public void onNext(T t) {

    }


}