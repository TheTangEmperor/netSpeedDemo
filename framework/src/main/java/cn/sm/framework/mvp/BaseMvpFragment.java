package cn.sm.framework.mvp;

import android.os.Bundle;

import androidx.annotation.Nullable;
import cn.sm.framework.base.BaseFragment;

/**
 * Describe：所有需要Mvp开发的Fragment的基类
 */

public abstract class BaseMvpFragment<P extends BasePresenter> extends BaseFragment implements IBaseView {

    protected P presenter;


    @SuppressWarnings("unchecked")
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //创建present
        presenter = createPresenter();
        if (presenter != null) {
            presenter.attachView(this);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (presenter != null) {
            presenter.detachView();
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
        showErrorMsg(errorMsg);
    }
    //***************************************IBaseView方法实现*************************************

    /**
     * 创建Presenter
     */
    protected abstract P createPresenter();
}
