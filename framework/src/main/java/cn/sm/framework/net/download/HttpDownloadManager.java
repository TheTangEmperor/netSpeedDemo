package cn.sm.framework.net.download;

import android.text.TextUtils;

import java.io.File;
import java.io.InterruptedIOException;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import cn.sm.framework.db.DbManager;
import cn.sm.framework.net.callback.ProgressDownSubscriber;
import cn.sm.framework.util.AppTools;
import cn.sm.framework.util.VerificationUtils;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class HttpDownloadManager {

    /**
     * 记录下载数据
     * */
    private Set<DownLoadInfo> downInfos = new HashSet<>();

    /**
     *  进度和状态的观察者
     */
    final private List<DownloadObserver> mObservers = new ArrayList<>();

    /**
     * 回调sub队列*/
    private HashMap<String,ProgressDownSubscriber<DownLoadInfo>> subMap = new HashMap<>();

    /**
     * 单例
     * */
    private volatile static HttpDownloadManager INSTANCE;

    private HttpDownloadManager() {
    }

    public static HttpDownloadManager getInstance(){
        if (INSTANCE == null){
            synchronized (HttpDownloadManager.class){
                if (INSTANCE == null) {
                    INSTANCE = new HttpDownloadManager();
                }
            }
        }
        return INSTANCE;
    }

    /**
     * 开始下载
     * @param info 要下载的应用信息
     */
    public void startAction(final DownLoadInfo info){
//        空的消息 和 已经在下载队列之中得消息均不处理
        if (info == null || downInfos.contains(info))return;
//        添加到任务队列
        downInfos.add(info);
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.connectTimeout(info.getConnectonTime(), TimeUnit.SECONDS);
        String baseUrl = VerificationUtils.urlSubBase(info.getUrl());
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .client(httpClient.build())
                .build();
        HttpDownService downService = retrofit.create(HttpDownService.class);

        final ProgressDownSubscriber<DownLoadInfo> subscriber = new ProgressDownSubscriber<>(info);
        subMap.put(info.getUrl(), subscriber);
        String replace = info.getUrl().replace(baseUrl, "");
//        JLog.e("baseUrl " + baseUrl + " replace: " + replace + " ReadLength: " + info.getReadLength());
        downService.download("bytes=" + info.getReadLength() + "-", replace)
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .map(new Func1<ResponseBody, DownLoadInfo>() {
                    @Override
                    public DownLoadInfo call(ResponseBody responseBody) {
                        try {
                            AppTools.writeCache(responseBody, new File(info.getSavePath()), info);
                        } catch (InterruptedIOException e) {
//                            e.printStackTrace();
                        }catch (SocketException e){
                            System.out.println("SocketException: " + e.getMessage());
                            info.setStateInte(DownLoadState.PAUSE.getState());
                            notifyDownloadStateChanged(info) ;
                        }catch (Exception e){
                            e.printStackTrace();
                            info.setStateInte(DownLoadState.ERROR.getState());
                            notifyDownloadStateChanged(info);
                        }
                        return info;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);

    }

    /**
     * 取消下载并删除
     * @param info 下载信息
     */
    public void cancelDelete(DownLoadInfo info){
        if (info == null)return;
//        先暂停
        pause(info);
        System.out.println("delete--------------");
//        删除数据库记录和本地缓存文件
        DbManager.getInstance().getDownloadDb().delete(info);
        new File(info.getSavePath()).delete();
        info.setReadLength(0);
        info.setStateInte(DownLoadState.NORMAL.getState());
        notifyDownloadStateChanged(info) ;

    }

    /**
     * 暂停下载
     * @param info
     */
    public void pause(DownLoadInfo info){
        if(info==null)return;
//      设置为暂停状态 并通知
        info.setStateInte(DownLoadState.PAUSE.getState());
        notifyDownloadStateChanged(info) ;
//        取消subscribe
        if(subMap.containsKey(info.getUrl())){
            ProgressDownSubscriber<DownLoadInfo> subscriber = subMap.get(info.getUrl());
            subscriber.unsubscribe();
            subMap.remove(info.getUrl());
        }
//        从下载队列中移除应用信息
        downInfos.remove(info);

//        这里需要将info信息写入到本地数据中，可自由扩展，用自己项目的数据库
        DbManager.getInstance().getDownloadDb().save(info);
    }


    /**
     * 移除下载数据
     * @param info
     */
    public void remove(DownLoadInfo info){
        downInfos.remove(info);
    }

    public Set<DownLoadInfo> getDownInfos() {
        return downInfos;
    }

    /** 注册观察者 */
    public void registerObserver(DownloadObserver observer) {
        synchronized (mObservers) {
            if (!mObservers.contains(observer)) {
                mObservers.add(observer);
//                JLog.e("新加入的观察者：" + observer);
            }
        }
    }

    /** 反注册观察者 */
    public void unRegisterObserver(DownloadObserver observer) {
        synchronized (mObservers) {
            //                JLog.e("移除：" + observer.toString() + " obSize: " + mObservers.size());
            mObservers.remove(observer);
        }
    }

    /** 当下载状态发送改变的时候回调 */
    public void notifyDownloadStateChanged(final DownLoadInfo info) {
        synchronized (mObservers) {
            Observable.just(1).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<Integer>() {
                @Override
                public void call(Integer integer) {
                    for (DownloadObserver observer : mObservers) {
                        DownLoadInfo obInfo = observer.getDownLoadInfo();
                        if (obInfo != null && !TextUtils.isEmpty(info.getPackageName()) && !TextUtils.isEmpty(obInfo.getPackageName())){
                            if (info.getPackageName().hashCode() == obInfo.getPackageName().hashCode()){
                                observer.onDownloadStateChanged(info);
                            }
                        }
                    }
                }
            });
        }
    }

    /** 当下载进度发送改变的时候回调 */
    public void notifyDownloadProgressed(final DownLoadInfo info) {
        synchronized (mObservers) {
            Observable.just(1).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<Integer>() {
                @Override
                public void call(Integer integer) {
                    for (DownloadObserver observer : mObservers) {
                        DownLoadInfo obInfo = observer.getDownLoadInfo();
                        if (obInfo != null && !TextUtils.isEmpty(info.getPackageName()) && !TextUtils.isEmpty(obInfo.getPackageName())){
                            if (info.getPackageName().hashCode() == obInfo.getPackageName().hashCode()){
                                observer.onDownloadProgressed(info);
                            }
                        }
                    }
                }
            });
        }
    }


}
