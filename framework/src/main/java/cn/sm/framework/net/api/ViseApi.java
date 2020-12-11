package cn.sm.framework.net.api;

import android.content.Context;

import com.google.gson.Gson;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import androidx.annotation.NonNull;
import cn.sm.framework.JConfig;
import cn.sm.framework.net.callback.ApiCallback;
import cn.sm.framework.net.callback.ApiCallbackSubscriber;
import cn.sm.framework.net.callback.DownloadListener;
import cn.sm.framework.net.interceptor.MyInterceptor;
import cn.sm.framework.net.parsing.ApiErrFunc;
import cn.sm.framework.net.parsing.ApiParsingFunc;
import cn.sm.framework.util.ClassUtil;
import cn.sm.framework.util.JLog;
import okhttp3.Cache;
import okhttp3.Interceptor;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.FieldMap;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * @Description: 网络操作入口
 * Created by lishimin 2017/4/19
 */
public class ViseApi {
    private static Context context;
    private static ApiService apiService;


    private ViseApi() {

    }


    /**
     * 普通Get方式请求，需传入实体类
     *
     * @param url
     * @param maps
     * @param clazz
     * @param <T>
     * @return
     */
    public <T> Observable<T> get(String url, Map<String, String> maps, Class<T> clazz) {
        return apiService.get(url, maps).compose(this.norTransformer(clazz));
    }

    /**
     * 普通Get方式请求，无需订阅，只需传入Callback回调
     *
     * @param url
     * @param maps
     * @param callback
     * @param <T>
     * @return
     */
    public <T> Subscription get(String url, Map<String, String> maps, ApiCallback<T> callback) {
        return this.get(url, maps, ClassUtil.getTClass(callback)).subscribe(new ApiCallbackSubscriber(context, callback));
    }


    /**
     * 普通POST方式请求，需传入实体类
     *
     * @param url
     * @param parameters
     * @param clazz
     * @param <T>
     * @return
     */
    public <T> Observable<T> post(final String url, Map<String, String> parameters, Class<T> clazz) {
        if (parameters == null) {
            parameters = new HashMap<>();
        }
        return apiService.post(url, parameters).compose(this.norTransformer(clazz));
    }

    /**
     * 普通POST方式请求，无需订阅，只需传入Callback回调
     *
     * @param url
     * @param maps
     * @param callback
     * @param <T>
     * @return
     */
    public <T> Subscription post(String url, Map<String, String> maps, ApiCallback<T> callback) {
        if (maps != null){
            System.out.println(new Gson().toJson(maps));
        }
        return this.post(url, maps, ClassUtil.getTClass(callback)).subscribe(new ApiCallbackSubscriber(context, callback));
    }

    /**
     * 普通POST方式请求，需传入实体类
     *
     * @param url
     * @param parameters
     * @param clazz
     * @param <T>
     * @return
     */
    public <T> Observable<T> post2(final String url, final Map<String, Object> parameters, Class<T> clazz) {
        return apiService.post2(url, parameters).compose(this.norTransformer(clazz));
    }

    /**
     * 普通POST方式请求，无需订阅，只需传入Callback回调
     *
     * @param url
     * @param maps
     * @param callback
     * @param <T>
     * @return
     */
    public <T> Subscription post2(String url, Map<String, Object> maps, ApiCallback<T> callback) {
        if (maps != null){
            System.out.println(new Gson().toJson(maps));
        }

        return this.post2(url, maps, ClassUtil.getTClass(callback)).subscribe(new ApiCallbackSubscriber(context, callback));
    }

    /**
     * 提交表单方式请求，需传入实体类
     *
     * @param url
     * @param fields
     * @param clazz
     * @param <T>
     * @return
     */
    public <T> Observable<T> form(final String url, final @FieldMap(encoded = true) Map<String, Object> fields, Class<T> clazz) {
        return apiService.postForm(url, fields).compose(this.norTransformer(clazz));
    }

    /**
     * 提交表单方式请求，无需订阅，只需传入Callback回调
     *
     * @param url
     * @param fields
     * @param callback
     * @param <T>
     * @return
     */
    public <T> Subscription form(final String url, final @FieldMap(encoded = true) Map<String, Object> fields, ApiCallback<T> callback) {
        return this.form(url, fields, ClassUtil.getTClass(callback)).subscribe(new ApiCallbackSubscriber(context, callback));
    }

    public <T> Observable<T> postBody(final String url, final RequestBody body, Class<T> clazz){
        return apiService.postJson(url, body).compose(this.norTransformer(clazz));
    }

    /**
     * 提交Body方式请求，无需订阅，只需传入Callback回调
     *
     * @param url
     * @param body
     * @param callback
     * @param <T>
     * @return
     */
    public <T> Subscription postBody(final String url, final RequestBody body, ApiCallback<T> callback) {
        return this.postBody(url, body, ClassUtil.getTClass(callback)).subscribe(new ApiCallbackSubscriber(context, callback));
    }


    /**
     * 同时添加body和query参数的post请求
     * @param url 地址
     * @param body body
     * @param maps query
     * @param callback 回调
     * @param <T> 数据类型
     * @return
     */
    public <T> Subscription postQueryAndBody(final String url, final RequestBody body, Map<String, String> maps, ApiCallback<T> callback){
        if (maps == null) maps = new HashMap<>();
        return apiService.postQueryAndBody(url, body, maps).compose(this.norTransformer(ClassUtil.getTClass(callback))).subscribe(new ApiCallbackSubscriber(context, callback));
    }


    /**
     * 只有query参数的post请求
     * @param url 地址
     * @param maps 参数
     * @param callback 回调
     * @param <T> 数据类型
     * @return 观察者
     */
    public <T> Subscription postQuery(final String url, Map<String,String> maps, ApiCallback<T> callback){
        if (maps == null) maps = new HashMap<>();
        return apiService.postQuery(url, maps).compose(this.norTransformer(ClassUtil.getTClass(callback))).subscribe(new ApiCallbackSubscriber(context, callback));
    }




    /**
     * 提交Body方式请求，需传入实体类
     *
     * @param url
     * @param body
     * @param clazz
     * @param <T>
     * @return
     */
    public <T> Observable<T> body(final String url, final Object body, Class<T> clazz) {
        return apiService.postBody(url, body).compose(this.norTransformer(clazz));
    }

    /**
     * 提交Body方式请求，无需订阅，只需传入Callback回调
     *
     * @param url
     * @param body
     * @param callback
     * @param <T>
     * @return
     */
    public <T> Subscription body(final String url, final Object body, ApiCallback<T> callback) {
        return this.body(url, body, ClassUtil.getTClass(callback)).subscribe(new ApiCallbackSubscriber(context, callback));
    }

    /**
     * 删除信息请求，需传入实体类
     *
     * @param url
     * @param maps
     * @param clazz
     * @param <T>
     * @return
     */
    public <T> Observable<T> delete(final String url, final Map<String, String> maps, Class<T> clazz) {
        return apiService.delete(url, maps).compose(this.norTransformer(clazz));
    }

    /**
     * 删除信息请求，无需订阅，只需传入Callback回调
     *
     * @param url
     * @param maps
     * @param callback
     * @param <T>
     * @return
     */
    public <T> Subscription delete(String url, Map<String, String> maps, ApiCallback<T> callback) {
        return this.delete(url, maps, ClassUtil.getTClass(callback)).subscribe(new ApiCallbackSubscriber(context, callback));
    }

    /**
     * 修改信息请求，需传入实体类
     *
     * @param url
     * @param maps
     * @param clazz
     * @param <T>
     * @return
     */
    public <T> Observable<T> put(final String url, final Map<String, String> maps, Class<T> clazz) {
        return apiService.put(url, maps).compose(this.norTransformer(clazz));
    }

    /**
     * 修改信息请求，无需订阅，只需传入Callback回调
     *
     * @param url
     * @param maps
     * @param callback
     * @param <T>
     * @return
     */
    public <T> Subscription put(String url, Map<String, String> maps, ApiCallback<T> callback) {
        return this.put(url, maps, ClassUtil.getTClass(callback)).subscribe(new ApiCallbackSubscriber(context, callback));
    }

    /**
     * 上传图片，需传入请求body和实体类
     *
     * @param url
     * @param requestBody
     * @param clazz
     * @param <T>
     * @return
     */
    public <T> Observable<T> uploadImage(String url, RequestBody requestBody, Map<String, String> maps, Class<T> clazz) {
        if (maps == null) maps = new HashMap<>();
        return apiService.uploadImage(url, requestBody, maps).compose(this.norTransformer(clazz));
    }

    /**
     * 上传图片，需传入图片文件和实体类
     *
     * @param url
     * @param file
     * @param clazz
     * @param <T>
     * @return
     */
    public <T> Observable<T> uploadImage(String url, File file, Map<String, String> maps, Class<T> clazz) {
        if (maps == null) maps = new HashMap<>();
        return apiService.uploadImage(url, RequestBody.create(okhttp3.MediaType.parse("image/jpg; " + "charset=utf-8"), file), maps).compose
                (this.norTransformer(clazz));
    }

    /**
     * 上传文件
     *
     * @param url
     * @param requestBody
     * @param file
     * @param clazz
     * @param <T>
     * @return
     */
    public <T> Observable<T> uploadFile(String url, RequestBody requestBody, MultipartBody.Part file, Class<T> clazz) {
        return apiService.uploadFile(url, requestBody, file).compose(this.norTransformer(clazz));
    }

    /**
     * 上传文件
     *
     * @param url 地址
     * @param maps query
     * @param file 文件
     * @param clazz 数据类型
     * @param <T> 数据类型
     * @return 观察者
     */
    public <T> Observable<T> uploadFile(String url, Map<String, String> maps, MultipartBody.Part file, Class<T> clazz) {
        return apiService.uploadFile(url, maps, file).compose(this.norTransformer(clazz));
    }


    /**
     * 上传多文件
     *
     * @param url
     * @param files
     * @param clazz
     * @param <T>
     * @return
     */
    public <T> Observable<T> uploadFlies(String url, Map<String, RequestBody> files, Class<T> clazz) {
        return apiService.uploadFiles(url, files).compose(this.norTransformer(clazz));
    }

    /**
     * 文件下载
     * @param url
     * @param dir
     * @param listener
     */
    public void downloadFile(String url,final String dir, final DownloadListener listener){
        Call<ResponseBody> call = apiService.downloadFile(url);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, final Response<ResponseBody> response) {
                new Thread(){
                    @Override
                    public void run() {
                        JLog.d("response: " + response.message());
                        writeResponseBodyToDisk2(dir, response.body(), listener);


                    }
                }.start();

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                if (listener != null) listener.onFailure();
            }
        });
    }

    private void writeResponseBodyToDisk2(String dir, ResponseBody body, @NonNull DownloadListener listener) {
        try{
            int oldProgress = 0;
            long totalLength = body.contentLength();
            long curSize = 0;
            //创建一个文件
            File futureStudioIconFile = new File(dir);
            RandomAccessFile randomAccessFile = new RandomAccessFile(futureStudioIconFile, "rwd");
            FileChannel fileChannel = randomAccessFile.getChannel();
            MappedByteBuffer mappedByteBuffer = fileChannel.map(FileChannel.MapMode.READ_WRITE, 0, totalLength);
            //设置每次读写的字节
            byte[] buffer = new byte[1024 * 8];
            int len;
            listener.onStart();
            while ((len = body.byteStream().read(buffer)) != -1){
                mappedByteBuffer.put(buffer, 0 , len);
                curSize += len;
                int curProg = (int) (100 * curSize / totalLength);
                if (curProg - oldProgress > 1){
                    listener.onProgress(curProg);
                    oldProgress = curProg;
                }
            }
            body.byteStream().close();
            if (fileChannel != null){
                fileChannel.close();
            }
            randomAccessFile.close();
            listener.onFinish(dir);
        }catch (Exception e){
            e.printStackTrace();
            listener.onFailure();
        }



    }

    /**
     * 写入到本地
     *
     * @param body 内容
     */
    private void writeResponseBodyToDisk(String dir, ResponseBody body, @NonNull DownloadListener listener) {
        try {

            //创建一个文件
            File futureStudioIconFile = new File(dir);
            //初始化输入流
            InputStream inputStream = null;
            //初始化输出流
            OutputStream outputStream = null;
            try {
                //设置每次读写的字节
                byte[] fileReader = new byte[4096];
                long totalLength = body.contentLength();
                long fileSizeDownloaded = 0;
                int oldProgress = 0;
                //请求返回的字节流
                inputStream = body.byteStream();
                //创建输出流
                outputStream = new FileOutputStream(futureStudioIconFile);
                JLog.d("load start");
                listener.onStart();
                //进行读取操作
                while (true) {
                    int read = inputStream.read(fileReader);
                    if (read == -1) {
                        break;
                    }
                    //进行写入操作
                    outputStream.write(fileReader, 0, read);
                    fileSizeDownloaded += read;
                    int curProg = (int) (100 * fileSizeDownloaded / totalLength);
                    if (curProg - oldProgress > 1){
                        listener.onProgress(curProg);
                        oldProgress = curProg;
                    }
                }
                //刷新
                outputStream.flush();
                System.out.println("size: " + fileSizeDownloaded);
                listener.onProgress(100);
                listener.onFinish(dir);

            } catch (Exception e) {
                e.printStackTrace();
                listener.onFailure();

            } finally {
                if (inputStream != null) {
                    //关闭输入流
                    inputStream.close();
                }
                if (outputStream != null) {
                    //关闭输出流
                    outputStream.close();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            listener.onFailure();
        }
    }


    /**
     * 创建ViseApi.Builder
     *
     * @param context
     * @return
     */
    public ViseApi.Builder newBuilder(Context context) {
        return new ViseApi.Builder(context);
    }

    /**
     * 设置请求线程和回调线程的自由切换 并将结果解析成对应的bean
     * @param <T>
     * @return
     */
    private <T> Observable.Transformer<ResponseBody,T> norTransformer (final Class<T> clazz){
        return new Observable.Transformer<ResponseBody, T>() {
            @Override
            public Observable<T> call(Observable<ResponseBody> tObservable) {
                return tObservable.subscribeOn(Schedulers.io()).unsubscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                        .map(new ApiParsingFunc<T>(clazz)).onErrorResumeNext(new ApiErrFunc<T>());
            }
        };
    }



    /**
     * 非空判断
     * @param t
     * @param message
     * @param <T>
     * @return
     */
    private static <T> T checkNotNull(T t, String message) {
        if (t == null) {
            throw new NullPointerException(message);
        }
        return t;
    }

    /**
     * ViseApi的所有配置都通过建造者方式创建
     */
    public static final class Builder {
        private Cache cache;
        private String baseUrl;
        private Boolean isCache = false;
        private int connectTimeout = JConfig.DEFAULT_CONNECT_TIMEOUT;
        private TimeUnit connectTimeUnit = TimeUnit.SECONDS;
        private int readTimeout = JConfig.DEFAULT_READ_TIMEOUT;
        private TimeUnit readTimeUnit = TimeUnit.SECONDS;
        private Retrofit.Builder retrofitBuilder;
        private OkHttpClient.Builder okHttpBuilder;

        public Builder(Context mContext) {
            context = mContext;
            retrofitBuilder = new Retrofit.Builder();
            okHttpBuilder = new OkHttpClient.Builder();
            okHttpBuilder.addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY));
        }


        public ViseApi build() {

            okHttpBuilder
                    .connectTimeout(connectTimeout,connectTimeUnit)
                    .writeTimeout(readTimeout,readTimeUnit)
                    .readTimeout(readTimeout,readTimeUnit);
            if (isCache) okHttpBuilder.cache(new Cache(context.getCacheDir(),JConfig.CACHE_MAX_SIZE));


            Retrofit retrofit = retrofitBuilder
                    .baseUrl(baseUrl)
                    .client(okHttpBuilder.build())
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                    .build();

            apiService = retrofit.create(ApiService.class);

            return new ViseApi();
        }


        /**
         * 设置连接超时时间（秒）
         *
         * @param seconds
         * @return
         */
        public ViseApi.Builder connectTimeout(int seconds) {
            this.connectTimeout = seconds;
            this.connectTimeUnit = TimeUnit.SECONDS;
            return this;
        }


        /**
         * 设置连接超时时间
         *
         * @param timeout
         * @param unit
         * @return
         */
        public ViseApi.Builder connectTimeout(int timeout, TimeUnit unit) {
            this.connectTimeout = timeout;
            this.connectTimeUnit = unit;
            return this;
        }


        /**
         * 设置读取和写入超时时间（默认单位是: 秒）
         *
         * @param timeout
         * @return
         */
        public ViseApi.Builder setReadAndWriteTimeOut(int timeout) {
            this.readTimeout = timeout;
            readTimeUnit = TimeUnit.SECONDS;
            return this;
        }


        /**
         * 设置读取和写入超时时间 (可接受自定义的时间单位)
         *
         * @param timeout
         * @param unit
         * @return
         */
        public ViseApi.Builder setReadAndWriteTimeOut(int timeout, TimeUnit unit) {
            if (timeout > -1) {
                readTimeout = timeout;
                readTimeUnit = unit;
            }
            return this;
        }


        /**
         * 设置是否添加缓存
         *
         * @param isCache
         * @return
         */
        public ViseApi.Builder setCacheEnable(boolean isCache) {
            this.isCache = isCache;
            return this;
        }


        /**
         * 设置请求BaseURL
         *
         * @param baseUrl
         * @return
         */
        public ViseApi.Builder baseUrl(String baseUrl) {
            this.baseUrl = checkNotNull(baseUrl, "baseUrl == null");
            return this;
        }



        /**
         * 设置请求头部
         *
         * @param headers
         * @return
         */
        public ViseApi.Builder headers(Map<String, String> headers) {
            if (headers != null){
                System.out.println(new Gson().toJson(headers));
            }
            okHttpBuilder.addInterceptor(new MyInterceptor(headers));
            return this;
        }


        /**
         * 设置请求参数
         *
         * @param parameters
         * @return
         */
        public ViseApi.Builder parameters(Map<String, String> parameters) {
            okHttpBuilder.addInterceptor(new MyInterceptor(parameters));
            return this;
        }


        /**
         * 设置拦截器
         *
         * @param interceptor
         * @return
         */
        public ViseApi.Builder interceptor(Interceptor interceptor) {
            okHttpBuilder.addInterceptor(checkNotNull(interceptor, "interceptor == null"));
            return this;
        }

        /**
         * 设置网络拦截器
         *
         * @param interceptor
         * @return
         */
        public ViseApi.Builder networkInterceptor(Interceptor interceptor) {
            okHttpBuilder.addNetworkInterceptor(checkNotNull(interceptor, "interceptor == null"));
            return this;
        }

    }
}
