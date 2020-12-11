package cn.sm.framework.net.callback;

import androidx.annotation.NonNull;
import cn.sm.framework.net.exception.ApiException;

/**
 * Created by lishiming on 17/4/18.
 */

public abstract class ApiCallback<T> {
    public void onStart(){};

    public abstract void onError(@NonNull ApiException e);

    public void onCompleted(){};

    public abstract void onNext(@NonNull T data);
}
