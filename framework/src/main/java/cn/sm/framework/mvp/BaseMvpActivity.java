package cn.sm.framework.mvp;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.Nullable;
import cn.sm.framework.base.BaseActivity;

/**
 * Describe：所有需要Mvp开发的Activity的基类
 */
@SuppressWarnings("unchecked")
public abstract class BaseMvpActivity<P extends BasePresenter> extends BaseActivity implements IBaseView {

    protected P presenter;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //创建present
        presenter = createPresenter();
        if (presenter != null) {
            presenter.attachView(this);
            initData();
        }
    }

    protected abstract void initData();

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (presenter != null) {
            presenter.detachView();
            presenter = null;
        }

    }


    //***************************************IBaseView方法实现*************************************
    @Override
    public void showLoading() {

    }


    @Override
    public void dismissLoading() {

    }

    @Override
    public void onEmpty(Object tag) {

    }

    @Override
    public void onError(Object tag, String errorMsg) {
        showToast(errorMsg);
    }

    @Override
    public Context getContext() {
        return this;
    }


    //***************************************IBaseView方法实现*************************************

    /**
     * 创建Presenter
     */
    protected abstract P createPresenter();
}
